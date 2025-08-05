package com.gdbsolutions.ribsapp.utils.html

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.FileProvider
import java.io.File
import androidx.core.graphics.createBitmap
import com.gdbsolutions.ribsapp.utils.converters.toFormattedDateTimeStringFile
import org.json.JSONObject

fun shareHtmlAsPdf(context: Context, html: String, fileName: String) {
    val activity = context.findActivity() ?: return
    val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
    val webView = WebView(context)

    // Configurações essenciais
    webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    webView.settings.javaScriptEnabled = true
    webView.settings.loadWithOverviewMode = true
    webView.settings.useWideViewPort = true

    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)
            Handler(Looper.getMainLooper()).postDelayed({
                view.post {
                    // Obter dimensões do conteúdo
                    view.evaluateJavascript("""
                    (function() {
                        var body = document.body, html = document.documentElement;
                        return {
                            width: Math.max(body.scrollWidth, body.offsetWidth, html.scrollWidth, html.offsetWidth),
                            height: Math.max(body.scrollHeight, body.offsetHeight, html.scrollHeight, html.offsetHeight)
                        };
                    })()
                """.trimIndent()) { dimensions ->
                        try {
                            val json = JSONObject(dimensions)
                            val contentWidth = json.getInt("width")
                            var contentHeight = (json.getInt("height"))
                            contentHeight = (contentHeight * 0.7).toInt()

                            if (contentWidth <= 0 || contentHeight <= 0) {
                                Log.e("PdfCreator", "Dimensões inválidas: $contentWidth x $contentHeight")
                                rootView.removeView(view)
                                return@evaluateJavascript
                            }

                            // Definir tamanho da WebView para o tamanho do conteúdo
                            val layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            view.layoutParams = layoutParams

                            // Medir e layout com as dimensões exatas
                            view.measure(
                                View.MeasureSpec.makeMeasureSpec(contentWidth, View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(contentHeight, View.MeasureSpec.EXACTLY)
                            )
                            view.layout(0, 0, contentWidth, contentHeight)

                            // Criar PDF com margens reduzidas
                            val margin = 10
                            val pdfWidth = contentWidth
                            val pdfHeight = contentHeight

                            val bitmap = createBitmap(pdfWidth, pdfHeight, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(bitmap)
                            canvas.drawColor(Color.WHITE)
                            canvas.translate(-20f, margin.toFloat())
                            view.draw(canvas)

                            val pdfDocument = PdfDocument()
                            val pageInfo = PdfDocument.PageInfo.Builder(pdfWidth, pdfHeight, 1).create()
                            val page = pdfDocument.startPage(pageInfo)
                            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                            pdfDocument.finishPage(page)

                            // Restante do código para salvar e compartilhar o PDF...
                            rootView.removeView(view)

                            val file = File(context.cacheDir, "${fileName}_${System.currentTimeMillis().toFormattedDateTimeStringFile()}.pdf")
                            try {
                                file.outputStream().use { pdfDocument.writeTo(it) }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                pdfDocument.close()
                            }

                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(
                                Intent.createChooser(shareIntent, "Compartilhar $fileName em PDF")
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        } catch (e: Exception) {
                            Log.e("PdfCreator", "Erro ao gerar PDF", e)
                            rootView.removeView(view)
                        }
                    }
                }
            }, 1500)
        }
    }

    // Adiciona a WebView fora da tela
    webView.x = -10000f
    rootView.addView(webView)
    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
