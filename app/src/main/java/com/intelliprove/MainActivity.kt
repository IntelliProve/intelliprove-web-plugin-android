package com.intelliprove

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.intelliprove.webview.IntelliWebViewActivity
import com.intelliprove.webview.IntelliWebViewDelegate

class MainActivity : AppCompatActivity(), IntelliWebViewDelegate {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val urlEditText = findViewById<EditText>(R.id.editTextText2)
        val openWebViewButton = findViewById<Button>(R.id.openWebViewButton)

        openWebViewButton.setOnClickListener {
            val url = urlEditText.text.toString().trim()
            if (url.isNotEmpty()) {
                openWebView(url)
            } else {
                Log.e("MainActivity", "URL is empty")
            }
        }
    }

    private fun openWebView(url: String) {
        IntelliWebViewActivity.start(
            this,
            url,
            this
        )
    }

    override fun didReceivePostMessage(postMessage: String) {
        Log.d("MainActivity", postMessage)
    }
}
