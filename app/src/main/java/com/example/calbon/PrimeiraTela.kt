package com.example.calbon

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calbon.databinding.ActivityPrimeiraTelaBinding

class PrimeiraTela : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        lateinit var binding: ActivityPrimeiraTelaBinding

        binding = ActivityPrimeiraTelaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ir para login
        binding.buttonLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

        }

        //ir para primeiro acesso
        binding.buttonPrimeiroAcesso.setOnClickListener {
            val intent = Intent(this, Primeiro_acesso::class.java)
            startActivity(intent)
            finish()
        }









    }
}