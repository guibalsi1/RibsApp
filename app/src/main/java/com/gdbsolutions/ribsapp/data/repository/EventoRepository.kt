package com.gdbsolutions.ribsapp.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.gdbsolutions.ribsapp.data.local.dao.AdicionaisDAO
import com.gdbsolutions.ribsapp.data.local.dao.CarnesDAO
import com.gdbsolutions.ribsapp.data.local.dao.EntradasDAO
import com.gdbsolutions.ribsapp.data.local.dao.EventosDAO
import com.gdbsolutions.ribsapp.data.local.database.AppDatabase
import com.gdbsolutions.ribsapp.data.local.entity.AdicionaisEventoCrossRef
import com.gdbsolutions.ribsapp.data.local.entity.Adicional
import com.gdbsolutions.ribsapp.data.local.entity.Carnes
import com.gdbsolutions.ribsapp.data.local.entity.CarnesEventoCrossRef
import com.gdbsolutions.ribsapp.data.local.entity.Entradas
import com.gdbsolutions.ribsapp.data.local.entity.EntradasEventoCrossRef
import com.gdbsolutions.ribsapp.data.local.entity.Evento
import com.gdbsolutions.ribsapp.data.local.entity.EventoCompleto
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EventoRepository(application: Application) {

    private val eventosDao: EventosDAO
    private val carnesDao: CarnesDAO
    private val entradasDao: EntradasDAO
    private val adicionaisDao: AdicionaisDAO

    val allEventos: Flow<List<Evento>>
    val allCarnes: Flow<List<Carnes>>
    val allEntradas: Flow<List<Entradas>>
    val allAdicionais: Flow<List<Adicional>>
    val allEventosCompleto: Flow<List<EventoCompleto>>

    private val executor: ExecutorService = Executors.newFixedThreadPool(4)

    init {
        val db = AppDatabase.getDatabase(application)
        eventosDao = db.eventosDao()
        carnesDao = db.carnesDao()
        entradasDao = db.entradasDao()
        adicionaisDao = db.adicionaisDao()

        allEventos = eventosDao.getAllEventos()
        allCarnes = carnesDao.getAllCarnes()
        allEntradas = entradasDao.getAllEntradas()
        allAdicionais = adicionaisDao.getAllAdicionais()
        allEventosCompleto = eventosDao.getAllEventosCompleto()
    }

    fun getEventoCompleto(id: Long): LiveData<EventoCompleto> {
        return eventosDao.getEventoCompletoById(id)
    }

    fun insertEvento(evento: Evento, onComplete: (Long) -> Unit) {
        executor.execute {
            val id = eventosDao.insertEvento(evento)
            onComplete(id)
        }
    }

    fun updateEvento(evento: Evento) {
        executor.execute {
            eventosDao.updateEvento(evento)
        }
    }

    fun deleteEvento(evento: Evento) {
        executor.execute {
            eventosDao.deleteEvento(evento)
            eventosDao.deleteCarnesDoEvento(evento.id)
            eventosDao.deleteEntradasDoEvento(evento.id)
            eventosDao.deleteAdicionaisDoEvento(evento.id)
        }
    }

    fun insertCarne(carne: Carnes) {
        executor.execute {
            carnesDao.insertCarne(carne)
        }
    }

    fun insertEntrada(entrada: Entradas) {
        executor.execute {
            entradasDao.insertEntrada(entrada)
        }
    }

    fun insertAdicional(adicional: Adicional) {
        executor.execute {
            adicionaisDao.insertAdicional(adicional)
        }
    }

    fun updateEntrada(entrada: Entradas) {
        executor.execute {
            entradasDao.updateEntrada(entrada)
        }
    }

    fun updateCarne(carne: Carnes) {
        executor.execute {
            carnesDao.updateCarne(carne)
        }
    }

    fun updateAdicional(adicional: Adicional) {
        executor.execute {
            adicionaisDao.updateAdicional(adicional)
        }
    }

    fun addCarneToEvento(eventoId: Long, carneId: Long) {
        executor.execute {
            val crossRef = CarnesEventoCrossRef(carneId, eventoId)
            eventosDao.insertCarneEvento(crossRef)
        }
    }

    fun addEntradaToEvento(eventoId: Long, entradaId: Long) {
        executor.execute {
            val crossRef = EntradasEventoCrossRef(entradaId, eventoId)
            eventosDao.insertEntradaEvento(crossRef)
        }
    }

    fun addAdicionalToEvento(eventoId: Long, adicionalId: Long) {
        executor.execute {
            val crossRef = AdicionaisEventoCrossRef(adicionalId, eventoId)
            eventosDao.insertAdicionalEvento(crossRef)
        }
    }
}