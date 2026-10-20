package com.neuronovaapps.rutinago.domain.communication

/** Editorial mapping only. Never search for a replacement or infer an ID from a label. */
fun CommunicationVocabularyItem.validatedPictogramId(): Long? =
    if (status == "VALIDADO" && visualStrategy !in setOf("SOLO_TEXTO", "PICTOGRAMA_PENDIENTE"))
        arasaacId.toLongOrNull()?.takeIf { it > 0 } else null

enum class CommunicationPictogramStatus { AVAILABLE, LOADING, NOT_STORED, FAILED, PENDING, TEXT_ONLY }

fun CommunicationVocabularyItem.pictogramFallback(): CommunicationPictogramStatus =
    if (visualStrategy == "SOLO_TEXTO") CommunicationPictogramStatus.TEXT_ONLY else CommunicationPictogramStatus.PENDING
