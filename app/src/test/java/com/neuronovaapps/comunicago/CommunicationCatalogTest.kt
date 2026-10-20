package com.neuronovaapps.comunicago

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.neuronovaapps.comunicago.data.communication.LocalCommunicationCatalogRepository
import java.io.File
import java.security.MessageDigest
import org.junit.Assert.*
import org.junit.Test

class CommunicationCatalogTest {
    private val root: File by lazy {
        generateSequence(File(requireNotNull(System.getProperty("user.dir"))).absoluteFile) { it.parentFile }
            .first { File(it, "migration/etapa2/source/docs/communication/source_v1.json").isFile }
    }
    private fun json() = JsonParser.parseString(
        File(root, "app/src/main/assets/${LocalCommunicationCatalogRepository.ASSET_PATH}").readText(),
    ).asJsonObject
    private fun repository(data: JsonObject = json()) = LocalCommunicationCatalogRepository { data.toString() }

    @Test fun connectorSnapshotRemainsTheVerifiedSource() {
        val hash = MessageDigest.getInstance("SHA-256")
            .digest(File(root, "migration/etapa2/source/docs/communication/source_v1.json").readText().replace("\r\n", "\n").toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
        assertEquals("4f49c92c6d1c569c8d50e9e0108ce5de1c1e7560b5105a3a91750286e5ed9df4", hash)
    }

    @Test fun approvedCountsAndAllStableIdsArePreserved() {
        val repo = repository()
        assertEquals(18, repo.categories.size)
        assertEquals(132, repo.vocabulary.size)
        assertEquals(119, repo.vocabulary.count { it.status == "VALIDADO" })
        assertEquals(13, repo.vocabulary.count { it.status == "PENDIENTE_ARASAAC" })
        assertEquals(13, repo.vocabulary.count { it.visualStrategy == "SOLO_TEXTO" })
        assertEquals(0, repo.vocabulary.count { it.visualStrategy == "PICTOGRAMA_PENDIENTE" })
        assertEquals(18, repo.categories.map { it.id }.toSet().size)
        assertEquals(132, repo.vocabulary.map { it.vocabId }.toSet().size)
        assertTrue(repo.categories.all { it.id.isNotBlank() && it.name.isNotBlank() })
        assertTrue(repo.vocabulary.all { it.vocabId.isNotBlank() && it.text.isNotBlank() })
        assertTrue(repo.vocabulary.all { word -> repo.categories.any { it.id == word.categoryId } })
    }

    @Test fun everySourceCellIncludingEmptyMetadataIsPreservedWithoutInference() {
        val source = JsonParser.parseString(File(root, "migration/etapa2/source/docs/communication/source_v1.json").readText())
            .asJsonObject.getAsJsonObject("sheets")
        val catalog = json()
        listOf("Categorías" to "categories", "Vocabulario" to "vocabulary").forEach { (sheet, key) ->
            val rows = source.getAsJsonObject(sheet).getAsJsonArray("values")
            val headers = rows[0].asJsonArray.map { it.asString }
            val records = catalog.getAsJsonArray(key)
            assertEquals(rows.size() - 1, records.size())
            records.forEachIndexed { i, record ->
                assertEquals(headers.toSet(), record.asJsonObject.keySet())
                val row = rows[i + 1].asJsonArray
                headers.forEachIndexed { column, header ->
                    val actual = record.asJsonObject[header]
                    val curated = setOf("arasaac_id", "estado", "estrategia_visual", "estado_curacion",
                        "fuente_curacion", "criterio_curacion", "criterio_estrategia_visual")
                    if (sheet == "Vocabulario" && header in curated) return@forEachIndexed
                    if (column < row.size()) assertEquals("$sheet row ${i + 2}/$header", row[column], actual)
                    else assertEquals("", actual.asString)
                }
            }
        }
    }

    @Test fun filteringAllAndEveryCategoryPreservesEditorialOrderAndPendingWords() {
        val repo = repository()
        assertEquals(repo.vocabulary, repo.vocabularyForCategory(null))
        assertEquals(repo.categories.sortedWith(compareBy({ it.order }, { it.id })), repo.categories)
        assertEquals(repo.vocabulary.sortedWith(compareBy({ it.order }, { it.vocabId })), repo.vocabulary)
        repo.categories.forEach { category ->
            assertTrue(category.order > 0)
            assertEquals(repo.vocabulary.filter { it.categoryId == category.id }, repo.vocabularyForCategory(category.id))
        }
        val allFiltered = repo.categories.flatMap { repo.vocabularyForCategory(it.id) }
        assertEquals(repo.vocabulary.toSet(), allFiltered.toSet())
        assertEquals(13, allFiltered.count { it.status == "PENDIENTE_ARASAAC" })
        assertTrue(repo.vocabulary.all { it.order > 0 })
        assertThrows(IllegalArgumentException::class.java) { repo.vocabularyForCategory("missing") }
    }

    @Test fun returnedCollectionsCannotBeMutated() {
        val repo = repository()
        assertThrows(UnsupportedOperationException::class.java) { (repo.categories as MutableList).clear() }
        assertThrows(UnsupportedOperationException::class.java) { (repo.vocabulary as MutableList).clear() }
        assertThrows(UnsupportedOperationException::class.java) {
            (repo.vocabularyForCategory(repo.categories.first().id) as MutableList).clear()
        }
    }

    @Test fun everyCuratedPairMatchesTheFinalUserDecisionsAndOriginalIdentity() {
        val final = JsonParser.parseString(File(root,
            "migration/etapa2/source/docs/communication/arasaac_audit/DECISIONES_FINALES_FASE_3_6_1.json").readText()).asJsonObject
        val backup = JsonParser.parseString(File(root,
            "migration/etapa2/source/docs/communication/arasaac_audit/communication_catalog_v1.original_pre_decisiones.json").readText()).asJsonObject
        val original = backup.getAsJsonArray("vocabulary").associate { it.asJsonObject["vocab_id"].asString to it.asJsonObject }
        val words = repository().vocabulary.associateBy { it.vocabId }
        val decisions = final.getAsJsonArray("decisions")
        assertEquals(132, decisions.size())
        assertEquals(132, decisions.map { it.asJsonObject["vocabId"].asString }.toSet().size)
        decisions.forEach { element ->
            val d = element.asJsonObject
            val w = requireNotNull(words[d["vocabId"].asString])
            assertEquals(d["word"].asString, w.text)
            assertEquals(d["categoryId"].asString, w.categoryId)
            if (d["candidateId"].isJsonNull) {
                assertEquals("", w.arasaacId); assertEquals("SOLO_TEXTO", w.visualStrategy)
                assertEquals("PENDIENTE_ARASAAC", w.status)
            } else {
                assertEquals(d["candidateId"].asString, w.arasaacId)
                assertEquals("VALIDADO", w.status); assertEquals("PICTOGRAMA_VALIDADO", w.visualStrategy)
                assertNotEquals(original.getValue(w.vocabId)["arasaac_id"].asString, w.arasaacId)
            }
        }
        listOf("VOC_072", "VOC_074", "VOC_097").forEach { assertEquals("SOLO_TEXTO", words.getValue(it).visualStrategy) }
    }

    @Test fun duplicateOrMissingRecordsAndBrokenReferencesFailAtomically() {
        rejects { it.getAsJsonArray("vocabulary").remove(0) }
        rejects { it.getAsJsonArray("categories").remove(0) }
        rejects { it.word().addProperty("vocab_id", it.getAsJsonArray("vocabulary")[1].asJsonObject["vocab_id"].asString) }
        rejects { it.category().addProperty("categoria_id", it.getAsJsonArray("categories")[1].asJsonObject["categoria_id"].asString) }
        rejects { it.word().addProperty("categoria_id", "CAT_999") }
    }

    @Test fun requiredIdentityLabelsAndPositiveIntegerOrdersAreStrict() {
        listOf("vocab_id", "texto").forEach { field -> rejects { it.word().addProperty(field, "") } }
        listOf("categoria_id", "categoria").forEach { field -> rejects { it.category().addProperty(field, "") } }
        listOf(-1, 0, 1.5).forEach { invalid ->
            rejects { it.word().addProperty("orden", invalid) }
            rejects { it.category().addProperty("orden", invalid) }
        }
        rejects { it.word().addProperty("orden", "1") }
        rejects { it.word().addProperty("vocab_id", "1") }
    }

    @Test fun sourceBooleansAndMetadataStructureCannotBeSilentlyCoercedOrDropped() {
        listOf("modo_simple", "modo_avanzado", "frase_rapida").forEach { field ->
            rejects { it.word().addProperty(field, "true") }
        }
        rejects { it.category().addProperty("activo", 1) }
        rejects { it.word().remove("observaciones") }
        rejects { it.word().addProperty("invented_field", "no") }
        rejects { it.addProperty("version", 2) }
        rejects { it.getAsJsonObject("counts").addProperty("vocabulary", 131) }
    }

    @Test fun changedCurationCountsOrInvalidArasaacMetadataAreRejected() {
        rejects { it.word().addProperty("estado", "PENDIENTE_ARASAAC") }
        rejects { it.word().addProperty("arasaac_id", "") }
        rejects { it.word().addProperty("arasaac_id", "inventado") }
        rejects { it.word().addProperty("estrategia_visual", "SOLO_TEXTO") }
        rejects { it.word().addProperty("estrategia_visual", "PICTOGRAMA_PENDIENTE") }
        rejects { it.word().addProperty("tratamiento_modo_simple", "inventado") }
        assertThrows(RuntimeException::class.java) { LocalCommunicationCatalogRepository { "{" } }
    }

    private fun JsonObject.word() = getAsJsonArray("vocabulary")[0].asJsonObject
    private fun JsonObject.category() = getAsJsonArray("categories")[0].asJsonObject
    private fun rejects(mutate: (JsonObject) -> Unit) {
        val data = json()
        mutate(data)
        assertThrows(RuntimeException::class.java) { repository(data) }
    }
}
