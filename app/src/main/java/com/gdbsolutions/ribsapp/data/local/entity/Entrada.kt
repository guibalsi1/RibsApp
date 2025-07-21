package com.gdbsolutions.ribsapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "entradas")
data class Entradas(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String = "Entrada",
    val descricao: String? = null,
    var ativo: Boolean = true
)

@Entity(primaryKeys = ["entradaId", "eventoId"],
    indices = [Index(value = ["eventoId"])])
data class EntradasEventoCrossRef(
    val entradaId: Long,
    val eventoId: Long
)