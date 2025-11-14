package com.moviles.huertohogar.data.database

import android.content.Context
import androidx.room.Database // Importaciones de Room
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.moviles.huertohogar.data.dao.UserDao
import com.moviles.huertohogar.data.dao.UserEntity
import com.moviles.huertohogar.domain.auth.UserRole
import com.moviles.huertohogar.data.dao.ProductDao
import com.moviles.huertohogar.data.dao.ProductEntity

// 1. CONVERTER: Clase para convertir el Enum UserRole a String (y viceversa) para Room
class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole) = value.name // Convierte Enum a String

    @TypeConverter
    fun toUserRole(value: String) = enumValueOf<UserRole>(value) // Convierte String a Enum
}

// 2. DATABASE: Clase principal de Room
@Database(
    entities = [UserEntity::class, ProductEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class) // Registra el conversor de tipos para toda la DB
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao // Método abstracto para obtener el DAO
    abstract fun productDao(): ProductDao

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

                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}