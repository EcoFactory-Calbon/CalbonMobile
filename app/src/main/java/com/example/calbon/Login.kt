package com.example.calbon

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailLayout = findViewById<TextInputLayout>(R.id.InputEmail)
        val senhaLayout = findViewById<TextInputLayout>(R.id.InputSenha)
        val voltar = findViewById<ImageView>(R.id.voltarRedefinirSenha)
        val continuar = findViewById<Button>(R.id.continuar)
        val primeiroAcesso = findViewById<TextView>(R.id.primeiroAcesso)
        val redefinirSenha = findViewById<TextView>(R.id.esqueciSenha)

        // Define cor do hint
        emailLayout.defaultHintTextColor = ColorStateList.valueOf(Color.WHITE)
        senhaLayout.defaultHintTextColor = ColorStateList.valueOf(Color.WHITE)

        voltar.setOnClickListener { finish() }

        continuar.setOnClickListener {
            val emailDigitado = emailLayout.editText?.text.toString().trim()
            val senhaDigitado = senhaLayout.editText?.text.toString().trim()

            // Limpa erros anteriores
            emailLayout.error = null
            senhaLayout.error = null

            // Valida campos vazios
            var erro = false
            if (emailDigitado.isEmpty()) {
                emailLayout.error = "Preencha o email"
                erro = true
            }
            if (senhaDigitado.isEmpty()) {
                senhaLayout.error = "Preencha a senha"
                erro = true
            }
            if (erro) return@setOnClickListener

            continuar.isEnabled = false

            // 1️⃣ Verifica se o email existe na base interna
            lifecycleScope.launch {
                try {
                    val usuarios = withContext(Dispatchers.IO) { apiUsuario.getUsers() }
                    val usuario = usuarios.find { it.email.equals(emailDigitado, ignoreCase = true) }

                    if (usuario == null) {
                        emailLayout.error = "Email não encontrado"
                        continuar.isEnabled = true
                        return@launch
                    }

                    // 2️⃣ Tenta autenticar no Firebase
                    auth.signInWithEmailAndPassword(emailDigitado, senhaDigitado)
                        .addOnCompleteListener { task ->
                            continuar.isEnabled = true
                            if (task.isSuccessful) {
                                // Recupera usuário da API
                                val usuario = usuarios.find { it.email.equals(emailDigitado, ignoreCase = true) }

                                if (usuario != null) {
                                    // Passa o objeto completo para a MainActivity
                                    val intent = Intent(this@Login, MainActivity::class.java)
                                    intent.putExtra("fragmentToLoad", "HomeFragment")
                                    intent.putExtra("user", usuario)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    emailLayout.error = "Usuário não encontrado"
                                }

                            } else {
                                senhaLayout.error = "Senha incorreta"
                            }
                        }


                } catch (e: Exception) {
                    e.printStackTrace()
                    emailLayout.error = "Erro ao consultar a API"
                    continuar.isEnabled = true
                }
            }
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
