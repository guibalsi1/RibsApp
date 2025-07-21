package com.gdbsolutions.ribsapp.utils.html

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.FileProvider
import java.io.File

fun shareHtmlAsPdf(context: Context, html: String) {
    val activity = context.findActivity()
    if (activity == null) {
        // Lidar com o erro: não foi possível encontrar a Activity
        return
    }

    val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
    val webView = WebView(context)

    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)

            // Adiciona um delay para garantir que o conteúdo seja totalmente renderizado
            Handler(Looper.getMainLooper()).postDelayed({
                // Mede a webview com base no seu conteúdo
                val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    View.MeasureSpec.UNSPECIFIED
                )
                val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    View.MeasureSpec.UNSPECIFIED
                )
                view.measure(widthMeasureSpec, heightMeasureSpec)
                view.layout(0, 0, view.measuredWidth, view.measuredHeight)


                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()
                val page = pdfDocument.startPage(pageInfo)

                view.draw(page.canvas)
                pdfDocument.finishPage(page)

                rootView.removeView(view)

                val file = File(context.cacheDir, "orcamento_${System.currentTimeMillis()}.pdf")
                try {
                    file.outputStream().use { pdfDocument.writeTo(it) }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pdfDocument.close()
                }

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(
                    Intent.createChooser(shareIntent, "Compartilhar Orçamento em PDF")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }, 1000) // Delay aumentado para 1000ms para segurança
        }
    }

    // A WebView precisa estar na hierarquia de views para renderizar,
    // mas pode ser posicionada fora da tela para não ser visível ao usuário.
    webView.x = -10000f
    rootView.addView(webView)

    webView.loadDataWithBaseURL("https://base.url", html, "text/html", "UTF-8", null)
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}