package com.gdbsolutions.ribsapp.utils.webview

import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberWebViewWithHtml(html: String): WebView {
    val context = LocalContext.current
    return remember(html) {
        WebView(context).apply {
            settings.javaScriptEnabled = false
            loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
        }
    }
}