// Archivo: app/src/main/java/com.moviles.huertohogar/data/remote/dto/ProductDto.kt

package com.moviles.huertohogar.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("precio") val precio: String,
    @SerializedName("categoria_id") val categoriaId: Int?,
    @SerializedName("imagen") val imagenUrl: String?,
    @SerializedName("stock") val stock: Int,
    @SerializedName("unidad") val unidad: String?,
    @SerializedName("destacado") val destacado: Boolean?,
    @SerializedName("tienda_id") val tiendaId: Int?,
    @SerializedName("categoria_nombre") val categoriaNombre: String?
)