package com.kiwisdr.mobile.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KiwiWebViewScreen() {
    val initialAssetUrl = "file:///android_asset/index.html"
    var isLoading by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    BackHandler(enabled = canGoBack) {
        webViewInstance?.goBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
    ) {
        // Top Toolbar
        Surface(
            color = Color(0xFF1F2937),
            tonalElevation = 4.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { webViewInstance?.goBack() },
                        enabled = canGoBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = if (canGoBack) Color.White else Color.DarkGray
                        )
                    }

                    IconButton(
                        onClick = { webViewInstance?.goForward() },
                        enabled = canGoForward
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Forward",
                            tint = if (canGoForward) Color.White else Color.DarkGray
                        )
                    }

                    IconButton(
                        onClick = { webViewInstance?.reload() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "KiwiSDR Mobile App",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (isLoading) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFF374151)
                    )
                }
            }
        }

        // WebView displaying bundled assets
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        allowFileAccess = true
                        allowContentAccess = true

                        cacheMode = WebSettings.LOAD_DEFAULT
                        setRenderPriority(WebSettings.RenderPriority.HIGH)
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        builtInZoomControls = true
                        displayZoomControls = false
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true
                            canGoBack = view?.canGoBack() ?: false
                            canGoForward = view?.canGoForward() ?: false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                            canGoBack = view?.canGoBack() ?: false
                            canGoForward = view?.canGoForward() ?: false
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            return false
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            progress = newProgress / 100f
                        }

                        override fun onPermissionRequest(request: PermissionRequest?) {
                            request?.grant(request.resources)
                        }
                    }

                    loadUrl(initialAssetUrl)
                    webViewInstance = this
                }
            },
            update = { webView ->
                canGoBack = webView.canGoBack()
                canGoForward = webView.canGoForward()
            },
            onRelease = { webView ->
                webView.stopLoading()
                webView.clearHistory()
                webView.clearCache(false)
                webView.removeAllViews()
                webView.destroy()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
