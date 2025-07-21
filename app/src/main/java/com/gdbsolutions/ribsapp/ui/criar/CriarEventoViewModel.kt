package com.gdbsolutions.ribsapp.ui.criar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.firstOrNull
import com.gdbsolutions.ribsapp.data.local.entity.Adicional
import com.gdbsolutions.ribsapp.data.local.entity.Carnes
import com.gdbsolutions.ribsapp.data.local.entity.Entradas
import com.gdbsolutions.ribsapp.data.local.entity.Evento
import com.gdbsolutions.ribsapp.data.local.entity.EventoCompleto
import com.gdbsolutions.ribsapp.data.repository.EventoRepository
import kotlinx.coroutines.launch

class CriarEventoViewModel(application: Application): AndroidViewModel(application) {
    private var switchEntradas = true
    private var switchCarnes = true
    private var switchAdicionais = true
    private val repository = EventoRepository(application)

    private val _carnes = MutableLiveData<List<Carnes>>()
    val carnes: LiveData<List<Carnes>> = _carnes
    fun carregarCarnes() {
        viewModelScope.launch {
            val lista = repository.allCarnes.firstOrNull() ?: emptyList()
            _carnes.value = lista
        }
    }

    private val _entradas = MutableLiveData<List<Entradas>>()
    val entradas: LiveData<List<Entradas>> = _entradas
    fun carregarEntradas() {
        viewModelScope.launch {
            val lista = repository.allEntradas.firstOrNull() ?: emptyList()
            _entradas.value = lista
        }
    }
    private val _adicionais = MutableLiveData<List<Adicional>>()
    val adicionais: LiveData<List<Adicional>> = _adicionais
    fun carregarAdicionais() {
        viewModelScope.launch {
            val lista = repository.allAdicionais.firstOrNull() ?: emptyList()
            _adicionais.value = lista
        }
    }

    private val _eventosCompleto = MutableLiveData<List<EventoCompleto>>()
    val eventosCompleto: LiveData<List<EventoCompleto>> = _eventosCompleto
    fun carregarEventosCompleto() {
        viewModelScope.launch {
            val lista = repository.allEventosCompleto.firstOrNull() ?: emptyList()
            _eventosCompleto.value = lista
        }
    }


    fun alternarAtivacaoGeralEntradas(novoValor: Boolean) {
        switchEntradas = novoValor
        _entradas.value = _entradas.value?.map { it.copy(ativo = novoValor) }
    }
    fun alternarAtivacaoGeralCarnes(novoValor: Boolean) {
        switchCarnes = novoValor
        _carnes.value = _carnes.value?.map { it.copy(ativo = novoValor) }
    }
    fun alternarAtivacaoGeralAdicionais(novoValor: Boolean) {
        switchAdicionais = novoValor
        _adicionais.value = _adicionais.value?.map { it.copy(ativo = novoValor) }
    }
    fun criarEventoComRelacoes(
        evento: Evento,
        carnesSelecionadas: List<Carnes>,
        entradasSelecionadas: List<Entradas>,
        adicionaisSelecionados: List<Adicional>
    ) {
        repository.insertEvento(evento) { eventoId ->
            carnesSelecionadas.forEach { carne ->
                repository.addCarneToEvento(eventoId, carne.id)
            }
            entradasSelecionadas.forEach { entrada ->
                repository.addEntradaToEvento(eventoId, entrada.id)
            }
            adicionaisSelecionados.forEach { adicional ->
                repository.addAdicionalToEvento(eventoId, adicional.id)
            }
        }
    }
    //fun updateEvento(evento: Evento) = repository.updateEvento(evento)
    fun deleteEvento(evento: Evento) {
        viewModelScope.launch {
            repository.deleteEvento(evento)
            carregarEventosCompleto()
        }
    }

    fun insertCarne(carne: Carnes) = repository.insertCarne(carne)
    fun insertEntrada(entrada: Entradas) = repository.insertEntrada(entrada)
    fun insertAdicional(adicional: Adicional) = repository.insertAdicional(adicional)

    fun atualizarStatusEntrada(index: Int, entradasAtuais: List<Entradas>, novoStatus: Boolean) {
        val novaLista = entradasAtuais.toMutableList()
        novaLista[index] = novaLista[index].copy(ativo = novoStatus)
        _entradas.value = novaLista
        viewModelScope.launch {
            repository.updateEntrada(novaLista[index])
        }
    }

    fun atualizarStatusCarne(index: Int, carnesAtuais: List<Carnes>, novoStatus: Boolean) {
        val novaLista = carnesAtuais.toMutableList()
        novaLista[index] = novaLista[index].copy(ativo = novoStatus)
        _carnes.value = novaLista
        viewModelScope.launch {
            repository.updateCarne(novaLista[index])
        }
    }

    fun atualizarStatusAdicional(index: Int, adicionaisAtuais: List<Adicional>, novoStatus: Boolean) {
        val novaLista = adicionaisAtuais.toMutableList()
        novaLista[index] = novaLista[index].copy(ativo = novoStatus)
        _adicionais.value = novaLista
        viewModelScope.launch {
            repository.updateAdicional(novaLista[index])
        }
    }

    //fun getEventoCompleto(id: Long): LiveData<EventoCompleto> = repository.getEventoCompleto(id)

    class EventoViewModelFactory(
        private val application: Application
    ) : ViewModelProvider.AndroidViewModelFactory(application) {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CriarEventoViewModel::class.java)) {
                return CriarEventoViewModel(application) as T
            }
            throw IllegalArgumentException("Classe ViewModel desconhecida")
        }
    }

}