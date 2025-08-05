package com.gdbsolutions.ribsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gdbsolutions.ribsapp.data.local.entity.Prato
import kotlinx.coroutines.flow.Flow

@Dao
interface PratosDAO {
    @Query("SELECT * FROM pratos")
    fun getAllPratos(): Flow<List<Prato>>

    @Query("SELECT * FROM pratos WHERE id = :id")
    fun getPratoById(id: Long): Flow<Prato>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPrato(prato: Prato)

    @Delete
    fun deletePrato(prato: Prato)

    @Update
    fun updatePrato(prato: Prato)



}