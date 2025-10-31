package com.example.calbon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calbon.util.SessionManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val videoView = findViewById<VideoView>(R.id.splashVideoView)

        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.splash}")
        videoView.setVideoURI(videoUri)
        videoView.start()

        videoView.setOnCompletionListener {
            if (SessionManager.getToken(this).isNullOrEmpty()) {
                // Ninguém logado → vai para a tela inicial
                startActivity(Intent(this, PrimeiraTela::class.java))
            } else {
                // Usuário logado → vai para a MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }
    }
}
