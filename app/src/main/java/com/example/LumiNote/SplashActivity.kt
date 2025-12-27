package com.example.LumiNote

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var ivLogo: ImageView
    private lateinit var tvAppName: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ivLogo = findViewById(R.id.ivLogo)
        tvAppName = findViewById(R.id.tvAppName)
        sessionManager = SessionManager(this)

        // Animasi fade in
        startFadeInAnimation()

        // Delay 3 detik lalu cek session
        Handler(Looper.getMainLooper()).postDelayed({
            checkSession()
        }, 3000)
    }

    private fun startFadeInAnimation() {
        // Animasi logo
        ObjectAnimator.ofFloat(ivLogo, "alpha", 0f, 1f).apply {
            duration = 1500
            start()
        }

        // Animasi app name (delay 500ms)
        Handler(Looper.getMainLooper()).postDelayed({
            ObjectAnimator.ofFloat(tvAppName, "alpha", 0f, 1f).apply {
                duration = 1500
                start()
            }
        }, 500)
    }

    private fun checkSession() {
        val intent = if (sessionManager.isLoggedIn()) {
            // Jika sudah login, langsung ke MainActivity
            Intent(this, MainActivity::class.java)
        } else {
            // Jika belum login, ke LoginActivity
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}