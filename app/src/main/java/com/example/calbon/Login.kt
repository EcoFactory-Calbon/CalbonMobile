package com.example.calbon

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val input = findViewById<TextInputLayout>(R.id.InputEmail)
        val voltar = findViewById<ImageView>(R.id.voltar)
        val continuar = findViewById<Button>(R.id.continuar)
        val primeiroAcesso = findViewById<TextView>(R.id.primeiroAcesso)
        val redefinirSenha = findViewById<TextView>(R.id.esqueciSenha)

        input.defaultHintTextColor = ColorStateList.valueOf(Color.WHITE)

        voltar.setOnClickListener{
            finish()
        }
        continuar.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fragmentToLoad", "HomeFragment")
            startActivity(intent)
            finish()
        }

        primeiroAcesso.setOnClickListener {
            val intent = Intent(this, Primeiro_acesso::class.java)
            startActivity(intent)
        }

        redefinirSenha.setOnClickListener {
            val intent = Intent(this, Redefinir_senha::class.java)
            startActivity(intent)
        }
    }
}
