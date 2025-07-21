package com.gdbsolutions.ribsapp.utils.converters

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Locale

fun String.textToBigdecimal(): BigDecimal {
    return try {
        val symbols = DecimalFormatSymbols(Locale("pt", "BR")).apply {
            decimalSeparator = ','
            groupingSeparator = '.'
        }

        val decimalFormat = DecimalFormat("#,##0.##", symbols).apply {
            isParseBigDecimal = true
        }

        decimalFormat.parse(this)?.let { it as BigDecimal } ?: BigDecimal.ZERO
    } catch (e: Exception) {
        BigDecimal.ZERO
    }
}

fun String.maskedTextToBigDecimal(): BigDecimal {
    return try {
        // Remove qualquer não-dígito
        val numeric = this.filter { it.isDigit() }

        if (numeric.isEmpty()) return BigDecimal.ZERO

        // Adiciona vírgula nos dois últimos dígitos
        val withDecimal = if (numeric.length == 1) {
            "0,0$numeric"
        } else if (numeric.length == 2) {
            "0,$numeric"
        } else {
            val reais = numeric.dropLast(2)
            val centavos = numeric.takeLast(2)
            "$reais,$centavos"
        }

        val symbols = DecimalFormatSymbols(Locale("pt", "BR")).apply {
            decimalSeparator = ','
            groupingSeparator = '.'
        }

        val decimalFormat = DecimalFormat("#,##0.##", symbols).apply {
            isParseBigDecimal = true
        }

        decimalFormat.parse(withDecimal)?.let { it as BigDecimal } ?: BigDecimal.ZERO
    } catch (e: Exception) {
        BigDecimal.ZERO
    }
}

fun BigDecimal.toStringBR(): String {
    val symbols = DecimalFormatSymbols(Locale("pt", "BR")).apply {
        decimalSeparator = ','
        groupingSeparator = '.'
    }

    val decimalFormat = DecimalFormat("#,##0.00", symbols)
    return decimalFormat.format(this)
}

fun Long.millisParaDataLocalSemFuso(): String {
    val utcDate = java.util.Date(this)
    val calendar = Calendar.getInstance()
    calendar.time = utcDate
    val dia = calendar.get(Calendar.DAY_OF_MONTH) + 1
    val mes = calendar.get(Calendar.MONTH) + 1
    val ano = calendar.get(Calendar.YEAR)
    return "%02d/%02d/%04d".format(dia, mes, ano)
}

fun Long.toFormattedDateTimeString(): String {
    val date = java.util.Date(this)
    val format = java.text.SimpleDateFormat("dd/MM/yyyy 'às' HH:mm")
    return format.format(date)
}