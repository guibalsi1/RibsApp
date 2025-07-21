package com.gdbsolutions.ribsapp.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.math.BigDecimal


@Entity(tableName = "eventos")
data class Evento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nomeEmpresa: String? = null,
    val dataEvento: String? = null,
    val localEvento: String? = null,
    val numPessoas: Long = 0,
    val precoPorPessoa: BigDecimal = BigDecimal.ZERO,
    val kmsRodados: Long = 0,
    val precoPorKm: BigDecimal = BigDecimal.ZERO,
    val observacoes: String? = null,
    val dataCriacao: Long = System.currentTimeMillis()
)

data class EventoCompleto (
    @Embedded val evento: Evento,
    @Relation(
        parentColumn = "id",
        entity = Entradas::class,
        entityColumn = "id",
        associateBy = Junction(
            value = EntradasEventoCrossRef::class,
            parentColumn = "eventoId",
            entityColumn = "entradaId"
        )
    )
    val entradas: List<Entradas> = emptyList(),
    @Relation(
        parentColumn = "id",
        entity = Carnes::class,
        entityColumn = "id",
        associateBy = Junction(
            value = CarnesEventoCrossRef::class,
            parentColumn = "eventoId",
            entityColumn = "carneId"
        )
    )
    val carnes: List<Carnes> = emptyList(),
    @Relation(
        parentColumn = "id",
        entity = Adicional::class,
        entityColumn = "id",
        associateBy = Junction(
            value = AdicionaisEventoCrossRef::class,
            parentColumn = "eventoId",
            entityColumn = "adicionalId"
        )
    )
    val adicionais: List<Adicional> = emptyList()
) {
    val precoTotal: BigDecimal
        get() = evento.precoPorPessoa.multiply(BigDecimal.valueOf(evento.numPessoas)) +
                evento.precoPorKm.multiply(BigDecimal.valueOf(evento.kmsRodados)) + precoAdicionais()

    fun precoAdicionais(): BigDecimal {
        val valorAdicionais: BigDecimal = adicionais.sumOf { it.valor }
        return valorAdicionais
    }
}
