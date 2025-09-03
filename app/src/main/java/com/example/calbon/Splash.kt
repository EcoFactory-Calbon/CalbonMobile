package com.example.calbon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calbon.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)


        lateinit var binding: ActivitySplashBinding


            binding = ActivitySplashBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.splash}")
            binding.splashVideoView.setVideoURI(videoUri)
            binding.splashVideoView.start()

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 10000)
        }

}

