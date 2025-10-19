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
import androidx.lifecycle.lifecycleScope
import com.example.calbon.api.RetrofitClient.apiUsuario
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Primeiro_acesso : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_primeiro_acesso)

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val voltar = findViewById<ImageView>(R.id.voltarPrimeiroAcesso)
        val emailLayout = findViewById<TextInputLayout>(R.id.InputEmail)
        val numCrachaLayout = findViewById<TextInputLayout>(R.id.InputNumCracha)
        val codigoEmpresaLayout = findViewById<TextInputLayout>(R.id.InputCódigoEmpresa)
        val continuar = findViewById<Button>(R.id.continuar)
        val login = findViewById<TextView>(R.id.login)

        voltar.setOnClickListener { finish() }

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