// Archivo: app/src/main/java/com.moviles.huertohogar/data/remote/dto/CategoryDto.kt

package com.moviles.huertohogar.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String?,


    @SerializedName("tienda_id") val tiendaId: Int?,
    @SerializedName("tienda_slug") val tiendaSlug: String?
)