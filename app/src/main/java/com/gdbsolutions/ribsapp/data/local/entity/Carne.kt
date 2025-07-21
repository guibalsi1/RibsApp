package com.gdbsolutions.ribsapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "carnes")
data class Carnes(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String = "Carne",
    val descricao: String? = null,
    var ativo: Boolean = true
)

@Entity(primaryKeys = ["carneId", "eventoId"],
    indices = [Index(value = ["eventoId"])])
data class CarnesEventoCrossRef(
    val carneId: Long,
    val eventoId: Long
)