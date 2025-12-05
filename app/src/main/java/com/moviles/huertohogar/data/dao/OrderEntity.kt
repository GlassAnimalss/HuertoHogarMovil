package com.moviles.huertohogar.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String, // Campo clave para filtrar por usuario
    val clientName: String,
    val date: String,
    val total: Double,
    val itemsSummary: String,
    val address: String,
    val status: String = "En Preparación"
)