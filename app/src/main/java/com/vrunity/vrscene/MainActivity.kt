package com.vrunity.vrscene

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Real VR needs WebXR, and WebXR only exists in a browser — an Android
        // WebView has none, which is why a plain WebView build never entered VR.
        // So the scene is handed to the device browser (the Meta Quest Browser on
        // a headset, which runs immersive VR). That address is this project's own
        // page, so the newest export is always what loads — the app updates
        // itself with no reinstall.
        val url = getString(R.string.scene_url)
        if (url.startsWith("http") && openInBrowser(url)) return
        // No browser on the device: play the copy bundled inside the app.
        val web = WebView(this)
        val s: WebSettings = web.settings
        s.javaScriptEnabled = true
        s.domStorageEnabled = true
        s.allowFileAccess = true
        s.mediaPlaybackRequiresUserGesture = false
        web.loadUrl("file:///android_asset/scene.html")
        setContentView(web)
    }

    private fun openInBrowser(url: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}
