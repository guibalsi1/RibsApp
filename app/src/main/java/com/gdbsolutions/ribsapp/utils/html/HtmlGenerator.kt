package com.gdbsolutions.ribsapp.utils.html

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.gdbsolutions.ribsapp.R
import com.gdbsolutions.ribsapp.data.local.entity.EventoCompleto
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import androidx.core.graphics.scale
import com.gdbsolutions.ribsapp.utils.bitmapParaBase64
import com.gdbsolutions.ribsapp.utils.converters.emReaisPorExtenso
import com.gdbsolutions.ribsapp.utils.gerarPix
import com.gdbsolutions.ribsapp.utils.gerarQrCodeBitmap
import java.math.RoundingMode


fun Context.drawableToBase64(drawableResId: Int): String? {
    val drawable = AppCompatResources.getDrawable(this, drawableResId) ?: return null
    val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: drawable.toBitmap()

    // Aumentar a resolução da imagem (2x o tamanho original)
    val scaledBitmap = bitmap.scale(bitmap.width * 2, bitmap.height * 2)

    val outputStream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

@RequiresApi(Build.VERSION_CODES.O)
fun EventoCompleto.toHtmlOrcamento(context: Context): String {
    val base64Logo = context.drawableToBase64(R.drawable.logo3x)

    val entradasHtml = entradas.joinToString("") {
        """
        <div class="item">
            <span><strong>${it.nome}</strong></span>
            <span class="muted">${it.descricao}</span>
        </div>
        """
    }

    val carnesHtml = carnes.joinToString("") {
        """
        <div class="item">
            <span><strong>${it.nome}</strong></span>
            <span class="muted">${it.descricao}</span>
        </div>
        """
    }

    val adicionaisHtml = adicionais.joinToString("") {
        """
        <div class="adicional">
            <span>${it.nome}</span>
            <span>${it.valor}</span>
        </div>
        """
    }
    val pratosHtml = pratos.joinToString("") {
        """
        <div class="item">
            <span><strong>${it.nome}</strong></span>
            <span class="muted">${it.descricao}</span>
        </div>
        """
    }

    val totalAdicionais = precoAdicionais()
    val valorPorPessoa = evento.precoPorPessoa
    val valorKm = evento.precoPorKm
    val totalPessoas = evento.numPessoas
    val totalKm = evento.kmsRodados

    val totalPessoasValor = evento.precoPorPessoa.multiply(BigDecimal.valueOf(evento.numPessoas))
    val totalKmValor = evento.precoPorKm.multiply(BigDecimal.valueOf(evento.kmsRodados))
    val totalFinal = totalPessoasValor + totalKmValor + totalAdicionais
    val codigoPix = gerarPix(totalFinal.setScale(2, RoundingMode.HALF_UP).toPlainString())
    val qrBitmap = gerarQrCodeBitmap(codigoPix)
    val qrBase64 = bitmapParaBase64(qrBitmap)

    val logoSrc = "data:image/png;base64,$base64Logo"

    val infoEvento = buildString {
        append("""
            <div class="info">
            """.trimIndent())
        if (evento.localEvento != null) {
         append("""
                <span><strong>Local do Evento:</strong> ${evento.localEvento}</span>
         """.trimIndent())
        }
        if (evento.dataEvento != null) {
            append("""
                <span><strong>Data do Evento:</strong> ${evento.dataEvento}</span>
            """.trimIndent())
        }
        if (evento.nomeEmpresa != null) {
            append("""
                <span><strong>Empresa:</strong> ${evento.nomeEmpresa}</span>
            """.trimIndent())
        }
        append("""
            </div>
        """.trimIndent())
    }

    // Construir as seções condicionalmente
    val entradasSection = if (entradas.isNotEmpty()) {
        """
        <div class="section" id="entradas">
            <h2>Entradas</h2>
            <div class="list">$entradasHtml</div>
        </div>
        """
    } else ""

    val carnesSection = if (carnes.isNotEmpty()) {
        """
        <div class="section" id="carnes">
            <h2>Carnes</h2>
            <div class="list">$carnesHtml</div>
        </div>
        """
    } else ""

    val adicionaisSection = if (adicionais.isNotEmpty()) {
        """
        <div class="section" id="adicionais">
            <h2>Adicionais</h2>
            <div class="list">$adicionaisHtml</div>
        </div>
        """
    } else ""
    val pratosSection = if (pratos.isNotEmpty()) {
        """
        <div class="section" id="pratos">
            <h2>Pratos Principais</h2>
            <div class="list">$pratosHtml</div>
        </div>
        """
    } else ""

    val observacoesSection = if (evento.observacoes != null) {
        """
        <div class="section" id="observacoes">
            <h2>Observações</h2>
            <div class="list">
                <div class="item">
                    <span>${evento.observacoes}</span>
                </div>
            </div>
            </div>
        """.trimIndent()
    } else ""

    // Construir a seção de valores condicionalmente
    val valoresSection = buildString {
        append("""
            <div class="section valores">
                <div>
                    <span>Valor por pessoa (R$${"%.2f".format(valorPorPessoa)}) x $totalPessoas pessoas</span>
                    <span><strong>R$ ${"%.2f".format(totalPessoasValor)}</strong></span>
                </div>
                <div>
                    <span>Valor por Km (R$${"%.2f".format(valorKm)}) x $totalKm km</span>
                    <span><strong>R$ ${"%.2f".format(totalKmValor)}</strong></span>
                </div>
        """)

        if (adicionais.isNotEmpty()) {
            append("""
                <div>
                    <span>Total dos adicionais</span>
                    <span><strong>R$ ${"%.2f".format(totalAdicionais)}</strong></span>
                </div>
            """)
        }

        append("""
                <div class="total">
                    <span>Valor Total</span>
                    <span>R$ ${"%.2f".format(totalFinal)}</span>
                </div>
            </div>
        """)
    }

    return """
        <!DOCTYPE html>
        <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <title>Orçamento do Evento</title>
                <style>
                @media print {
                    body {
                        width: 210mm;
                        height: 297mm;
                    }
                }
                    html, body {
                            width: 100%;
                            height: auto;
                            margin: 0;
                            padding: 0;
                        }
                    .wrapper {
                            width: 100%;
                            max-width: 800px;
                            margin: 0 auto;
                            padding: 10px;
                            box-sizing: border-box;
                        }
                    .header {
                        display: flex; justify-content: space-between; align-items: center;
                        border-bottom: 2px solid #ccc; padding-bottom: 10px; margin-bottom: 20px;
                    }
                    .header img { 
                        max-height: 80px;  /* Aumente este valor conforme necessário */
                        width: auto;
                        image-rendering: -webkit-optimize-contrast; /* Melhora a renderização */
                        image-rendering: crisp-edges;
                    }
                    .header .info { text-align: right; }
                    .title { text-align: center; font-size: 20px; font-weight: bold; margin-bottom: 20px; }
                    .info { display: flex; justify-content: space-between; align-items: center; }
                    .info span { display: block; }
                    .section { margin-bottom: 15px; }
                    .section h2 {
                        font-size: 16px; color: #444;
                        border-bottom: 1px solid #ddd; padding-bottom: 4px; margin-bottom: 10px;
                    }
                    .list { display: grid; grid-template-columns: 1fr 1fr; gap: 10px 40px; }
                    .item { margin-bottom: 4px; }
                    .item span { display: block; }
                    .adicional {
                        display: flex; justify-content: space-between;
                        border-bottom: 1px dashed #ccc; padding: 4px 0;
                    }
                    .valores {
                        background: #f7f7f7; padding: 10px; border-radius: 8px; border: 1px solid #ccc;
                    }
                    .valores div {
                        margin-bottom: 5px; display: flex; justify-content: space-between;
                    }
                    .total {
                        font-weight: bold; font-size: 16px; color: #222;
                        border-top: 2px solid #999; padding-top: 6px;
                    }
                    .pagamento {
                        margin-top: 20px; border-top: 1px solid #ccc; padding-top: 10px; height: 80px;
                    }
                    .pix {
                        display: flex; flex-direction: column; justify-content: space-between; align-items: center;
                        border-bottom: 1px solid #ccc; padding-bottom: 4px;
                    }
                    .pix img { max-height: 200px; }
                    .muted { color: #aaa; font-style: italic; }
                </style>
            </head>
            <body>
                <div class="wrapper">
                    <div class="header">
                        <img src="$logoSrc" alt="Logo da Empresa">
                            <div class="info">
                                <strong>Ribs Smoked Barbecue</strong><br>
                                CNPJ: 39.758.012/0001-15
                            </div>
                    </div>
                    <div class="title">Orçamento do Evento</div>
                    $infoEvento
                    $entradasSection
                    $carnesSection
                    $pratosSection
                    $adicionaisSection
                    $observacoesSection
                    $valoresSection
                    <div class="section pagamento">
                        <h2>Pagamento</h2>
                        <div class="pix">
                            <p>PIX Copia e Cola</p>
                            <span><strong>$codigoPix</strong></span>
                            <div class="qr-code">
                                <img src="data:image/png;base64,$qrBase64" alt="QR Code Pix">
                            </div>
                        </div>
                    </div>
                </div>
            </body>
        </html>
        """.trimIndent()
}

fun EventoCompleto.toHtmlCardapio(context: Context): String {
    val base64Logo = context.drawableToBase64(R.drawable.logo3x)
    val base64knife = context.drawableToBase64(R.drawable.knife)
    val base64restaurant = context.drawableToBase64(R.drawable.restaurant)
    val base64salad = context.drawableToBase64(R.drawable.salad)
    val knifeSrc = "data:image/png;base64,$base64knife"
    val restaurantSrc = "data:image/png;base64,$base64restaurant"
    val saladSrc = "data:image/png;base64,$base64salad"

    // Seção de Carnes
    val carnesHtml = if (carnes.isNotEmpty()) {
        """
            <h2 class="section-title">
            <img src="$knifeSrc" class="icon" alt="Carnes" />
            CARNES
            </h2>
                ${carnes.joinToString("") {
            """
                <div class="item">- ${it.nome}<span>${it.descricao}</span></div>
            """
        }}
        """
    } else ""

    val entradasHtml = if (entradas.isNotEmpty()) {
        """
            <h2 class="section-title">
            <img src="$saladSrc" class="icon" alt="Entradas" />
            ENTRADAS
            </h2>
                ${entradas.joinToString("") {
            """
                <div class="item">- ${it.nome}<span>${it.descricao}</span></div>
            """
        }}
        """
    } else ""
    val pratosHtml = if (pratos.isNotEmpty()) {
        """
            <h2 class="section-title">
            <img src="$restaurantSrc" class="icon" alt="Pratos" />
            PRATOS PRINCIPAIS
            </h2>
                ${pratos.joinToString("") {
            """
                <div class="item">- ${it.nome}<span>${it.descricao}</span></div>
            """
        }}
        """
    } else ""

    val logoSrc = "data:image/png;base64,$base64Logo"

    return """
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head>
          <meta charset="UTF-8" />
          <link rel="preconnect" href="https://fonts.googleapis.com">
          <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
          <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@200..800&display=swap" rel="stylesheet">
          <title>Cardápio</title>
          <style>
          @media print {
              body {
                  width: 210mm;
                  height: 297mm;
              }
          }
            html, body {
              width: 100%;
              height: auto;
              margin: 0;
              padding: 0;
            }
            body {
              font-family: 'Manrope', sans-serif;
              background: #fff;
              color: #000;
              margin: 0;
              padding: 10px 0px 0px 40px;
              max-width: 1000px;
            }
        
            header {
              display: flex;
              align-items: center;
              justify-content: space-between;
              margin-bottom: 30px;
              border-bottom: 2px solid #000;
              padding-bottom: 10px;
            }
        
            .logo {
              display: flex;
              align-items: center;
              gap: 10px;
            }
        
            .logo img {
              width: 120px;
              height: 120px;
              border-radius: 50%;
            }
        
            .logo-title {
              font-size: 30px;
              font-weight: 600;
            }
        
            .title-wrapper {
              display: flex;
              justify-content: center;
              align-items: center;
              margin-bottom: 30px;
            }
        
            .title-wrapper span {
              background: #1a1a1a;
              color: white;
              padding: 8px 16px;
              font-weight: bold;
              border-radius: 4px;
              font-size: 40px;
            }
        
            h2 {
              font-size: 24px;
              margin-top: 40px;
              margin-bottom: 10px;
            }
            
            h2 img {
              width: 32px;
              height: 32px;
              margin-right: 8px;
            }
        
            .section-title {
              font-weight: 700;
              font-size: 30px;
              margin-bottom: 20px;
            }
        
            .item {
              border-bottom: 1px solid #333;
              padding: 8px 0;
              font-size: 36px;
            }
        
            .item span {
              font-size: 24px;
              color: #555;
              display: block;
              margin-top: 2px;
            }
          </style>
        </head>
        <body>
        
          <header>
            <div class="logo">
              <img src="$logoSrc" alt="Logo Ribs" />
              <div class="logo-title">Ribs Smoked Barbecue</div>
            </div>
          </header>
        
          <div class="title-wrapper">
            <span>Cardápio</span>
          </div>
        
          <section>
            $entradasHtml
          </section>
          <section>
            $pratosHtml
          </section>
          <section>
            $carnesHtml
          </section>
        </body>
        </html>
        """.trimIndent()
}

fun EventoCompleto.toHtmlRecibo(context: Context): String {
    val base64Logo = context.drawableToBase64(R.drawable.logo3x)
    val logoSrc = "data:image/png;base64,$base64Logo"
    return """
        <!DOCTYPE html>
        <html lang="pt-BR">
        <head>
          <meta charset="UTF-8" />
          <link rel="preconnect" href="https://fonts.googleapis.com">
          <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
          <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@200..800&display=swap" rel="stylesheet">
          <title>Cardápio</title>
          <style>
          @media print {
              body {
                  width: 210mm;
                  height: 297mm;
              }
          }
            html, body {
              width: 100%;
              height: auto;
              margin: 0;
              padding: 0;
            }
            body {
              font-family: 'Manrope', sans-serif;
              background: #fff;
              color: #000;
              margin: 0;
              padding: 10px 0px 0px 40px;
              max-width: 1000px;
            }
        
            header {
              display: flex;
              align-items: center;
              justify-content: space-between;
              margin-bottom: 30px;
              border-bottom: 2px solid #000;
              padding-bottom: 10px;
            }
        
            .logo {
              display: flex;
              align-items: center;
              gap: 10px;
            }
        
            .logo img {
              width: 120px;
              height: 120px;
              border-radius: 50%;
            }
        
            .logo-title {
              font-size: 30px;
              font-weight: 600;
            }
        
            .title-wrapper {
              display: flex;
              justify-content: center;
              align-items: center;
              margin-bottom: 30px;
            }
            .wrapper {
                            width: 100%;
                            max-width: 800px;
                            margin: 0 auto;
                            padding: 10px;
                            box-sizing: border-box;
                        }
            .title-wrapper span {
              padding: 8px 16px;
              font-weight: bold;
              border-radius: 4px;
              font-size: 20px;
            }
        
            h2 {
              font-size: 24px;
              margin-top: 40px;
              margin-bottom: 10px;
            }
            p {
              font-size: 24px;
              margin-bottom: 10px;
            }
          </style>
        </head>
        <body>
        
          <header>
            <div class="logo">
              <img src="$logoSrc" alt="Logo Ribs" />
              <div class="logo-title">Ribs Smoked Barbecue</div>
            </div>
          </header>
          <div class="title-wrapper">
            <span>Recibo de Pagamento</span>
          </div>
          <div class="wrapper">
            <p> Recebi da Empresa ${evento.nomeEmpresa}, o valor de R$ ${"%.2f".format(precoTotal)} (${precoTotal.emReaisPorExtenso()}) referente ao evento corporativo realizado no dia ${evento.dataEvento}.</p>
          </div>
        </body>
        </html>
        """.trimIndent()
}