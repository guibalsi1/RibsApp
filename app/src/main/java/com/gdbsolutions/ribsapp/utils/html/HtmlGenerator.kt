package com.gdbsolutions.ribsapp.utils.html

import com.gdbsolutions.ribsapp.data.local.entity.Carnes
import com.gdbsolutions.ribsapp.data.local.entity.Entradas
import com.gdbsolutions.ribsapp.data.local.entity.EventoCompleto
import java.math.BigDecimal

object EmpresaPrestadora {
    const val nome = "Ribs Smoked Barbecue"
    const val cnpj = "39.758.012/0001-15"
    var logoUrl: String = "https://live.staticflickr.com/65535/54667143221_cdb9920735_t.jpg"
}

fun EventoCompleto.toHtmlOrcamento(): String {
    val empresa = EmpresaPrestadora
    val totalAdicionais = precoAdicionais()
    val totalPessoas = evento.precoPorPessoa.multiply(BigDecimal.valueOf(evento.numPessoas))
    val totalKm = evento.precoPorKm.multiply(BigDecimal.valueOf(evento.kmsRodados))
    val total = precoTotal

    fun formatCurrency(valor: BigDecimal?) = "R$ %.2f".format(valor).replace('.', ',')

    fun renderListaEntradas(titulo: String, itens: List<Entradas>): String {
        if (itens.isEmpty()) return ""
        return """
            <div class="section">
              <div class="section-title">$titulo</div>
              <div class="grid">
                ${itens.joinToString("") {
            """<div class="item"><strong>${it.nome}</strong><br>${it.descricao ?: ""}</div>"""
        }}
              </div>
            </div>
        """
    }

    fun renderListaCarnes(titulo: String, itens: List<Carnes>): String {
        if (itens.isEmpty()) return ""
        return """
            <div class="section">
              <div class="section-title">$titulo</div>
              <div class="grid">
                ${itens.joinToString("") {
            """<div class="item"><strong>${it.nome}</strong><br>${it.descricao ?: ""}</div>"""
        }}
              </div>
            </div>
        """
    }


    fun renderAdicionais(): String {
        if (adicionais.isEmpty()) return ""
        return """
            <div class="section">
              <div class="section-title">Adicionais</div>
              <div class="grid">
                ${adicionais.joinToString("") {
            """<div class="item"><strong>${it.nome}</strong><br>${it.descricao ?: ""}<br><em>${formatCurrency(it.valor)}</em></div>"""
        }}
              </div>
            </div>
        """
    }

    return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <style>
            body { font-family: 'Segoe UI', sans-serif; padding: 24px; margin: 0; }
            .header, .footer { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
            .logo { height: 60px; }
            .title { font-size: 28px; text-align: center; margin: 30px 0; font-weight: bold; }
            .section { margin-bottom: 24px; }
            .section-title { font-size: 20px; font-weight: 600; margin-bottom: 12px; border-bottom: 2px solid #ccc; padding-bottom: 6px; }
            .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 16px; }
            .item { background: #f9f9f9; padding: 12px; border-radius: 8px; }
            .valor-final { font-size: 22px; font-weight: bold; color: #1d3557; margin-top: 12px; }
          </style>
        </head>
        <body>
          <div class="header">
            <div>
              <div><strong>${empresa.nome}</strong></div>
              <div>CNPJ: ${empresa.cnpj}</div>
            </div>
          </div>

          <div class="title">Orçamento do Evento</div>

          ${renderListaEntradas("Entradas", entradas)}
          ${renderListaCarnes("Carnes", carnes)}
          ${renderAdicionais()}

          <div class="section">
            <div class="section-title">Valores</div>
            <p>Valor por pessoa (${evento.numPessoas} pessoas): ${formatCurrency(totalPessoas)}</p>
            <p>Valor por KM (${evento.kmsRodados} km): ${formatCurrency(totalKm)}</p>
            <p>Total de adicionais: ${formatCurrency(totalAdicionais)}</p>
            <p class="valor-final">Valor total do orçamento: ${formatCurrency(total)}</p>
          </div>

          <div class="section">
            <div class="section-title">Pagamento</div>
            <p><!-- espaço reservado --></p>
          </div>
        </body>
        </html>
    """.trimIndent()
}