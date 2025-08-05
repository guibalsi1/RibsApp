package com.gdbsolutions.ribsapp.utils

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.EnumMap


// Função para formatar um campo no padrão ID-Tamanho-Valor
private fun formatarCampo(id: String, valor: String): String {
    val tamanho = valor.length.toString().padStart(2, '0')
    return "$id$tamanho$valor"
}

// Função principal para gerar o payload completo
fun gerarPix(valor: String) : String {
    val semCrc = "00020126360014br.gov.bcb.pix0114+5514982195154520458125303986" + formatarCampo("54", valor) + "5802BR5919Fabio Souto Pereira6009Sao Paulo62070503***6304"
    val crc = calcularCRC16(semCrc)
    return semCrc + crc
}

fun calcularCRC16(input: String): String {
    var crc = 0xFFFF
    val polinomio = 0x1021

    val bytes = input.toByteArray(Charsets.UTF_8)

    for (b in bytes) {
        var data = b.toInt() shl 8
        for (i in 0 until 8) {
            val bit = ((data xor crc) and 0x8000) != 0
            crc = crc shl 1
            if (bit) {
                crc = crc xor polinomio
            }
            data = data shl 1
        }
    }

    crc = crc and 0xFFFF
    return String.format("%04X", crc)
}

fun gerarQrCodeBitmap(texto: String, tamanho: Int = 300): Bitmap {
    val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
    hints[EncodeHintType.CHARACTER_SET] = "UTF-8"

    val bitMatrix = QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, tamanho, tamanho, hints)
    val largura = bitMatrix.width
    val altura = bitMatrix.height

    val pixels = IntArray(largura * altura)
    for (y in 0 until altura) {
        for (x in 0 until largura) {
            val preto = bitMatrix.get(x, y)
            pixels[y * largura + x] = if (preto) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
        }
    }

    return Bitmap.createBitmap(pixels, largura, altura, Bitmap.Config.ARGB_8888)
}

@RequiresApi(Build.VERSION_CODES.O)
fun bitmapParaBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return Base64.getEncoder().encodeToString(outputStream.toByteArray())
}