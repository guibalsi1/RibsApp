package com.gdbsolutions.ribsapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal


@Entity(tableName = "adicionais")
data class Adicional(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String = "Adicional",
    val descricao: String? = null,
    val valor: BigDecimal = BigDecimal.ZERO,
    var ativo: Boolean = true
)


@Entity(
    primaryKeys = ["adicionalId", "eventoId"],
    indices = [Index(value = ["eventoId"])]
)
data class AdicionaisEventoCrossRef(
    val adicionalId: Long,
    val eventoId: Long
)
