package com.example.calbon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class Primeiro_acesso : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_primeiro_acesso)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val voltar = findViewById<ImageView>(R.id.voltarPrimeiroAcesso)
        val email = findViewById<TextInputLayout>(R.id.InputEmail)
        val numCracha = findViewById<TextInputLayout>(R.id.InputNumCracha)
        val codigoEmpresa = findViewById<TextInputLayout>(R.id.InputCódigoEmpresa)
        val continuar = findViewById<Button>(R.id.continuar)
        val login =  findViewById<TextView>(R.id.login)
        voltar.setOnClickListener{
            finish()
        }

        continuar.setOnClickListener{
            val intent = Intent(this, DefinirSenha::class.java)
            startActivity(intent)
            finish()
        }

        login.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}