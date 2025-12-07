package com.moviles.huertohogar.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
// Imports de DAOs
import com.moviles.huertohogar.data.dao.UserDao
import com.moviles.huertohogar.data.dao.ProductDao
import com.moviles.huertohogar.data.dao.OrderDao
// Imports de Entidades
import com.moviles.huertohogar.data.dao.UserEntity
import com.moviles.huertohogar.data.dao.ProductEntity
import com.moviles.huertohogar.data.dao.OrderEntity
// Import de Roles
import com.moviles.huertohogar.domain.auth.UserRole

// 1. CONVERSORES DE TIPO (Para guardar el Rol de Usuario)
class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole) = value.name

    @TypeConverter
    fun toUserRole(value: String) = enumValueOf<UserRole>(value)
}

// 2. DEFINICIÓN DE LA BASE DE DATOS
@Database(
    // Lista de todas las tablas: Usuarios, Productos y Pedidos
    entities = [UserEntity::class, ProductEntity::class, OrderEntity::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // 3. DAOs (Acceso a datos)
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao // Nuevo DAO para pedidos

    // 4. SINGLETON (Instancia única)
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "huertohogar_db"
                )
                    // Permite consultas en el hilo principal (Solo para desarrollo/debug rápido)
                    .allowMainThreadQueries()
                    // Si cambias la estructura (version 2->3), borra la DB antigua y crea una nueva
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}