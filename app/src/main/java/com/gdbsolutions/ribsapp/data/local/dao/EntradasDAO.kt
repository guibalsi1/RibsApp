package com.gdbsolutions.ribsapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gdbsolutions.ribsapp.data.local.entity.Entradas
import kotlinx.coroutines.flow.Flow

@Dao
interface EntradasDAO {
    @Query("SELECT * FROM entradas")
    fun getAllEntradas(): Flow<List<Entradas>>

    @Query("SELECT * FROM entradas WHERE id = :id")
    fun getEntradaById(id: Long): LiveData<Entradas>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEntrada(entrada: Entradas)

    @Delete
    fun deleteEntrada(entrada: Entradas)

    @Update
    fun updateEntrada(entrada: Entradas)

}