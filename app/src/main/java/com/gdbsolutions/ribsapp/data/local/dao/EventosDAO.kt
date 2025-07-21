package com.gdbsolutions.ribsapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gdbsolutions.ribsapp.data.local.entity.AdicionaisEventoCrossRef
import com.gdbsolutions.ribsapp.data.local.entity.CarnesEventoCrossRef
import com.gdbsolutions.ribsapp.data.local.entity.EntradasEventoCrossRef
import com.gdbsolutions.ribsapp.data.local.entity.Evento
import com.gdbsolutions.ribsapp.data.local.entity.EventoCompleto
import kotlinx.coroutines.flow.Flow

@Dao
interface EventosDAO {
    @Query("SELECT * FROM eventos")
    fun getAllEventos(): Flow<List<Evento>>

    @Query("SELECT * FROM eventos WHERE id = :id")
    fun getEventoById(id: Long): LiveData<Evento>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCarneEvento(carneEvento: CarnesEventoCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntradaEvento(entradaEventoCrossRef: EntradasEventoCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAdicionalEvento(adicionalEventoCrossRef: AdicionaisEventoCrossRef)

    @Query("DELETE FROM CarnesEventoCrossRef WHERE eventoId = :eventoId")
    fun deleteCarnesDoEvento(eventoId: Long)

    @Query("DELETE FROM EntradasEventoCrossRef WHERE eventoId = :eventoId")
    fun deleteEntradasDoEvento(eventoId: Long)

    @Query("DELETE FROM AdicionaisEventoCrossRef WHERE eventoId = :eventoId")
    fun deleteAdicionaisDoEvento(eventoId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvento(evento: Evento): Long

    @Transaction
    @Query("SELECT * FROM eventos WHERE id = :id")
    fun getEventoCompletoById(id: Long): LiveData<EventoCompleto>

    @Transaction
    @Query("SELECT * FROM eventos")
    fun getAllEventosCompleto(): Flow<List<EventoCompleto>>

    @Delete
    fun deleteEvento(evento: Evento)

    @Update
    fun updateEvento(evento: Evento)

}