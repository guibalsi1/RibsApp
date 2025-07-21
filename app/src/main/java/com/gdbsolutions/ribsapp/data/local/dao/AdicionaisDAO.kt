package com.gdbsolutions.ribsapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gdbsolutions.ribsapp.data.local.entity.Adicional
import kotlinx.coroutines.flow.Flow

@Dao
interface AdicionaisDAO {

    @Query("SELECT * FROM adicionais")
    fun getAllAdicionais(): Flow<List<Adicional>>

    @Query("SELECT * FROM adicionais WHERE id = :id")
    fun getAdicionalById(id: Long): LiveData<Adicional>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAdicional(adicional: Adicional)

    @Delete
    fun deleteAdicional(adicional: Adicional)

    @Update
    fun updateAdicional(adicional: Adicional)


}