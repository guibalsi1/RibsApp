package com.gdbsolutions.ribsapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity (tableName = "pratos")
data class Prato (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String = "Prato",
    val descricao: String? = null,
    var ativo: Boolean = true
)

@Entity(primaryKeys = ["pratoId", "eventoId"],
    indices = [Index(value = ["eventoId"])])
data class PratoEventoCrossRef(
    val pratoId: Long,
    val eventoId: Long
)