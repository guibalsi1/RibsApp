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
    // Tenta encontrar a Activity a partir do Contexto. É crucial para obter a view raiz.
    val activity = context.findActivity()
    if (activity == null) {
        // Se não encontrar a Activity, não podemos continuar.
        // Adicione aqui uma mensagem de erro para o usuário se desejar.
        return
    }

    // Pega a view raiz da atividade.
    val rootView = activity.findViewById<ViewGroup>(android.R.id.content)

    // 1. Cria a WebView.
    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)

            // Adiciona um pequeno delay para garantir que o layout esteja finalizado
            Handler(Looper.getMainLooper()).postDelayed({
                val a4Width = 792
                val a4Height = 1122

                view.measure(
                    View.MeasureSpec.makeMeasureSpec(a4Width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(a4Height, View.MeasureSpec.EXACTLY)
                )
                view.layout(0, 0, a4Width, a4Height)

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
            }, 300) // Delay de 300ms (ajustável)
        }
    }

    // 2. Define um layout com tamanho 0 para que a WebView não seja visível.
    val layoutParams = ViewGroup.LayoutParams(0, 0)
    webView.layoutParams = layoutParams

    // 3. Anexa a WebView à view raiz da sua tela. ESTE É O PASSO CRÍTICO.
    rootView.addView(webView)

    // Finalmente, carrega o HTML para iniciar todo o processo.
    webView.loadDataWithBaseURL("https://base.url", html, "text/html", "UTF-8", null)
}

// Função auxiliar para encontrar a Activity a partir do Context.
// Adicione esta função ao seu arquivo.
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}