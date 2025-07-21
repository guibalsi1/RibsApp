package com.gdbsolutions.ribsapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gdbsolutions.ribsapp.data.local.entity.Carnes
import kotlinx.coroutines.flow.Flow

@Dao
interface CarnesDAO {
    @Query("SELECT * FROM carnes")
    fun getAllCarnes(): Flow<List<Carnes>>

    @Query("SELECT * FROM carnes WHERE id = :id")
    fun getCarneById(id: Long): LiveData<Carnes>

    @Update
    fun updateCarne(carne: Carnes)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCarne(carne: Carnes)

    @Delete
    fun deleteCarne(carne: Carnes)


}