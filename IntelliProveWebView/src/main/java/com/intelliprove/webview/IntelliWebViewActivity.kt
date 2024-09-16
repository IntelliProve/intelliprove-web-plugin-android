package com.intelliprove.webview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONObject

interface IntelliWebViewDelegate {
    fun didReceivePostMessage(postMessage: String)
}

private object IntelliWebViewDelegateHolder {
    var delegate: IntelliWebViewDelegate? = null
}

class IntelliWebViewActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private var pendingPermissionRequest: PermissionRequest? = null

    private lateinit var webView: WebView;

    companion object {
        private const val urlStringKey = "urlStringKey"

        fun start(
            context: Context,
            urlString: String,
            delegate: IntelliWebViewDelegate?
        ) {
            IntelliWebViewDelegateHolder.delegate = delegate
            val intent = Intent(context, IntelliWebViewActivity::class.java).apply {
                putExtra(urlStringKey, urlString)
            }
            context.startActivity(intent)
        }
    }

    override fun onDestroy() {
        IntelliWebViewDelegateHolder.delegate = null
        this.webView.destroy()
        super.onDestroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_intelliwebview)

        val urlString = intent.getStringExtra(urlStringKey)
        this.webView = findViewById<WebView>(R.id.webView)

        // Needed to run JavaScript and use the LocalStorage API - otherwise the web app won't work
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        WebView.setWebContentsDebuggingEnabled(true)

        // Add a listener for the PostMessage API
        webView.addJavascriptInterface(IntelliWebAppInterface(this), "IntelliPostMessage")

        // Add a listener for WebView events, so we can inject some JavaScript at load time
        webView.webViewClient = IntelliWebViewClient()

        // Add listener for permissions, so we can dispatch the Camera Permissions check to Android
        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                consoleMessage?.let {
                    Log.d("WebView", "${it.sourceId()}:${it.lineNumber()} - ${it.message()}")
                }
                return super.onConsoleMessage(consoleMessage)
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.resources?.forEach { resource ->
                    when (resource) {
                        PermissionRequest.RESOURCE_VIDEO_CAPTURE -> {
                            // Check if CAMERA permission is granted
                            if (ContextCompat.checkSelfPermission(
                                    this@IntelliWebViewActivity,
                                    Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED) {
                                // Permission already granted, grant the camera permission
                                request.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                            } else {
                                // Request CAMERA permission from the user
                                pendingPermissionRequest = request
                                ActivityCompat.requestPermissions(
                                    this@IntelliWebViewActivity,
                                    arrayOf(Manifest.permission.CAMERA),
                                    CAMERA_PERMISSION_REQUEST_CODE
                                )
                            }
                        }
                        else -> {
                            // Default handling: grant permissions for other resource requests
                            request.grant(arrayOf(resource))
                        }
                    }
                }
            }
        }

        // Load URL in WebView
        urlString?.let { webView.loadUrl(it) }
    }

    // When we need to ask permission for camera usage, we need to eventually patch its result through to the web app
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, grant the camera permission to the WebView
                pendingPermissionRequest?.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
            } else {
                // Permission denied, deny the camera permission to the WebView
                pendingPermissionRequest?.deny()
            }
            pendingPermissionRequest = null
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}

private class IntelliWebAppInterface(val webViewActivity: IntelliWebViewActivity) {
    @JavascriptInterface
    fun receivePostMessage(postMessage: String) {
        Log.d("PostMessage", postMessage)

        val stage = stageFromJson(postMessage)
        if (stage == "dismiss") {
            webViewActivity.finish()
        } else {
            IntelliWebViewDelegateHolder.delegate?.didReceivePostMessage(postMessage)
        }
    }

    private fun stageFromJson(jsonString: String): String? {
        return try {
            val jsonObject = JSONObject(jsonString)
            jsonObject.getString("stage")
        } catch (e: Exception) {
            null
        }
    }
}

private class IntelliWebViewClient : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.d("WebView", "Page finished loading: $url")

        // Disable zoom
        val disableZoomJS = """
            var meta = document.createElement('meta');
            meta.name = 'viewport';
            meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no';
            var head = document.getElementsByTagName('head')[0];
            head.appendChild(meta);
        """.trimIndent()
        view?.evaluateJavascript(disableZoomJS, null)

        // Patch PostMessage API
        val postMessageJS = """
            window.postMessage = function(data) {
                var jsonString = JSON.stringify(data)
                window.IntelliPostMessage.receivePostMessage(jsonString);
            };
        """.trimIndent()
        view?.evaluateJavascript(postMessageJS, null)
    }
}