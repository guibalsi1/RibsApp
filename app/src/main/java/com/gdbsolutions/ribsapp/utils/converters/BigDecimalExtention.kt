package com.gdbsolutions.ribsapp.utils.converters

import java.math.BigDecimal
import java.math.RoundingMode

// Arrays para ajudar na conversão dos números
private val unidades = arrayOf(
    "", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove"
)

private val dezenasEspeciais = arrayOf(
    "dez", "onze", "doze", "treze", "catorze", "quinze", "dezesseis", "dezessete", "dezoito", "dezenove"
)

private val dezenas = arrayOf(
    "", "dez", "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa"
)

private val centenas = arrayOf(
    "", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos", "seiscentos", "setecentos", "oitocentos", "novecentos"
)

/**
 * Converte um número de até 3 dígitos em sua forma por extenso.
 * @param numero O número (0 a 999) a ser convertido.
 * @return O texto correspondente.
 */
private fun converterGrupoDeTresDigitos(numero: Int): String {
    if (numero == 0) return ""
    if (numero == 100) return "cem"

    val c = numero / 100
    val d = (numero % 100) / 10
    val u = numero % 10

    val partes = mutableListOf<String>()

    if (c > 0) partes.add(centenas[c])

    if (d > 0) {
        if (d == 1) {
            partes.add(dezenasEspeciais[u])
        } else {
            partes.add(dezenas[d])
            if (u > 0) partes.add(unidades[u])
        }
    } else if (u > 0) {
        partes.add(unidades[u])
    }

    return partes.joinToString(" e ")
}


/**
 * Função de extensão para a classe BigDecimal que converte seu valor para a representação
 * por extenso em moeda brasileira (Reais e Centavos).
 *
 * Exemplo de uso:
 * ```
 * val valor = BigDecimal("1234.56")
 * println(valor.emReaisPorExtenso()) // "um mil e duzentos e trinta e quatro reais e cinquenta e seis centavos"
 * ```
 */
fun BigDecimal.emReaisPorExtenso(): String {
    // Garante que o número tem duas casas decimais para os centavos
    val valorNormalizado = this.setScale(2, RoundingMode.HALF_UP)

    // Trata o caso especial de zero
    if (valorNormalizado.compareTo(BigDecimal.ZERO) == 0) {
        return "zero reais"
    }

    val reais = valorNormalizado.toLong()
    val centavos = valorNormalizado.remainder(BigDecimal.ONE).multiply(BigDecimal(100)).toInt()

    val parteReaisExtenso = converterParteInteira(reais)
    val parteCentavosExtenso = converterGrupoDeTresDigitos(centavos)

    return buildString {
        // Parte dos REAIS
        if (parteReaisExtenso.isNotEmpty()) {
            append(parteReaisExtenso)
            append(if (reais == 1L) " real" else " reais")
        }

        // Conector "e" entre reais e centavos
        if (parteReaisExtenso.isNotEmpty() && parteCentavosExtenso.isNotEmpty()) {
            append(" e ")
        }

        // Parte dos CENTAVOS
        if (parteCentavosExtenso.isNotEmpty()) {
            append(parteCentavosExtenso)
            append(if (centavos == 1) " centavo" else " centavos")
        }
    }
}

private fun converterParteInteira(valor: Long): String {
    if (valor == 0L) return ""

    val escalas = listOf(
        "",
        " mil",
        if (valor / 1_000_000 == 1L) " milhão" else " milhões",
        if (valor / 1_000_000_000 == 1L) " bilhão" else " bilhões"
        // Adicione mais escalas se necessário (trilhão, etc.)
    )

    var numero = valor
    var escalaIndex = 0
    val partes = mutableListOf<String>()

    while (numero > 0) {
        val grupo = (numero % 1000).toInt()
        if (grupo > 0) {
            val grupoExtenso = converterGrupoDeTresDigitos(grupo)
            // Lógica para "um mil" vs "mil"
            val textoFinalGrupo = if (escalaIndex == 1 && grupo == 1) {
                escalas[escalaIndex] // Retorna " mil"
            } else {
                "$grupoExtenso${escalas[escalaIndex]}"
            }
            partes.add(0, textoFinalGrupo)
        }
        numero /= 1000
        escalaIndex++
    }

    return partes.joinToString(" e ").trim()
}