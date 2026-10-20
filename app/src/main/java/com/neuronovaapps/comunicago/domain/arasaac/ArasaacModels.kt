package com.neuronovaapps.comunicago.domain.arasaac

enum class ArasaacImageResolution(val pixels: Int) {
    THUMBNAIL(300), PREVIEW(500),
}

object ArasaacImageUrls {
    fun forPictogram(id: Long, resolution: ArasaacImageResolution): String {
        require(id > 0) { "El ID de ARASAAC debe ser positivo." }
        return "https://static.arasaac.org/pictograms/$id/${id}_${resolution.pixels}.png"
    }
}
