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
            "https://plugin-streaming-dev.intelliprove.com/?action_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImVtYWlsIjoiIiwiY3VzdG9tZXIiOiJERU1PLUNVU1RPTUVSLTEiLCJncm91cCI6ImFkbWluIiwibWF4X21lYXN1cmVtZW50X2NvdW50IjotMX0sIm1ldGEiOnt9LCJleHAiOjE3MTQ5MDcyMDV9.7GRRK8zIs4Q_LJ_pDSBVljd6O4K2shfMBxZCmn4UOlM",
            this
        )
    }

    override fun didReceivePostMessage(postMessage: String) {
        Log.d("MainActivity", postMessage)
    }
}