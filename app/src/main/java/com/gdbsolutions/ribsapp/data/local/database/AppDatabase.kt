package com.gdbsolutions.ribsapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gdbsolutions.ribsapp.data.local.dao.AdicionaisDAO
import com.gdbsolutions.ribsapp.data.local.dao.CarnesDAO
import com.gdbsolutions.ribsapp.data.local.dao.EntradasDAO
import com.gdbsolutions.ribsapp.data.local.dao.EventosDAO
import com.gdbsolutions.ribsapp.data.local.dao.PratosDAO
import com.gdbsolutions.ribsapp.data.local.entity.AdicionaisEventoCrossRef
import com.gdbsolutions.ribsapp.data.local.entity.Adicional
import com.gdbsolutions.ribsapp.data.local.entity.Carnes
import com.gdbsolutions.ribsapp.data.local.entity.CarnesEventoCrossRef
import com.gdbsolutions.ribsapp.data.local.entity.Entradas
import com.gdbsolutions.ribsapp.data.local.entity.EntradasEventoCrossRef
import com.gdbsolutions.ribsapp.data.local.entity.Evento
import com.gdbsolutions.ribsapp.data.local.entity.Prato
import com.gdbsolutions.ribsapp.data.local.entity.PratoEventoCrossRef
import com.gdbsolutions.ribsapp.utils.converters.BigDecimalConverter

@Database(
    entities = [
        Entradas::class,
        Carnes::class,
        Adicional::class,
        Prato::class,
        Evento::class,
        EntradasEventoCrossRef::class,
        CarnesEventoCrossRef::class,
        PratoEventoCrossRef::class,
        AdicionaisEventoCrossRef::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(BigDecimalConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entradasDao(): EntradasDAO
    abstract fun carnesDao(): CarnesDAO
    abstract fun adicionaisDao(): AdicionaisDAO
    abstract fun eventosDao(): EventosDAO
    abstract fun pratosDao(): PratosDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ribs_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}