package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.GeminiService
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ERPWebViewScreen()
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ERPWebViewScreen() {
    val coroutineScope = rememberCoroutineScope()
    AndroidView(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    useWideViewPort = true
                    loadWithOverviewMode = true
                }
                
                // Expose AndroidBridge with support for Gemini API
                addJavascriptInterface(WebAppInterface(this, coroutineScope), "AndroidBridge")
                
                loadUrl("file:///android_asset/www/index.html")
            }
        }
    )
}

class WebAppInterface(
    private val webView: WebView,
    private val scope: CoroutineScope
) {
    private val geminiService = GeminiService()

    @JavascriptInterface
    fun callGemini(prompt: String, callbackJs: String) {
        scope.launch(Dispatchers.Main) {
            val response = try {
                geminiService.generateGenericContent(prompt)
            } catch (e: Exception) {
                "Error calling Gemini AI: ${e.localizedMessage ?: "Unknown error"}"
            }
            // Escape special character sequences to prevent invalid Javascript syntax injections
            val escapedResponse = response
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                
            webView.evaluateJavascript("javascript:$callbackJs('$escapedResponse')", null)
        }
    }
}
