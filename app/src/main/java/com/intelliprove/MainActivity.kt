package com.intelliprove

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.intelliprove.webview.IntelliWebViewActivity
import com.intelliprove.webview.IntelliWebViewDelegate

class MainActivity : AppCompatActivity(), IntelliWebViewDelegate {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val openWebViewButton = findViewById<Button>(R.id.openWebViewButton)
        openWebViewButton.setOnClickListener {
            openWebView()
        }
    }

    private fun openWebView() {
        IntelliWebViewActivity.start(
            this,
            "https://plugin-dev.intelliprove.com/?action_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImVtYWlsIjoiIiwiY3VzdG9tZXIiOiJOZWJ1bGFlIHRlc3RpbmciLCJncm91cCI6ImFkbWluIiwibWF4X21lYXN1cmVtZW50X2NvdW50IjoxMDAwfSwibWV0YSI6e30sImV4cCI6MTcxNTA3NDIyMn0.kQvGQD_8wFzmLjgFMuft_i3nWjAxSKWx5oI_FBFEYXI",
            this
        )
    }

    override fun didReceivePostMessage(postMessage: String) {
        Log.d("MainActivity", postMessage)
    }
}