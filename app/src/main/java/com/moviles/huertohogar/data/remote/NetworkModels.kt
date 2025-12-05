package com.moviles.huertohogar.data.remote

import com.google.gson.annotations.SerializedName

// Esta clase representa CÓMO viene el dato desde Internet.
// Fíjate que "imagen" es String (una URL), no un Int como en tu base de datos local.
data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("precio") val precio: Int,
    @SerializedName("stock") val stock: Int,
    @SerializedName("imagen") val imagenUrl: String?,
    @SerializedName("descripcion") val descripcion: String?
)