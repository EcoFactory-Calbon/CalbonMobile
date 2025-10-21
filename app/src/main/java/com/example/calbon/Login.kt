package com.example.calbon

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.calbon.api.LoginRequest
import com.example.calbon.api.RetrofitClient.apiUsuario
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Ajuste de padding com insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Views
        val emailLayout = findViewById<TextInputLayout>(R.id.InputEmail)
        val senhaLayout = findViewById<TextInputLayout>(R.id.InputSenha)
        val voltar = findViewById<ImageView>(R.id.voltarRedefinirSenha)
        val continuar = findViewById<Button>(R.id.continuar)
        val primeiroAcesso = findViewById<TextView>(R.id.primeiroAcesso)
        val redefinirSenha = findViewById<TextView>(R.id.esqueciSenha)

        emailLayout.defaultHintTextColor = ColorStateList.valueOf(Color.WHITE)
        senhaLayout.defaultHintTextColor = ColorStateList.valueOf(Color.WHITE)

        // Botão voltar
        voltar.setOnClickListener { finish() }

        // Botão continuar → login normal via API
        continuar.setOnClickListener {
            val emailDigitado = emailLayout.editText?.text.toString().trim()
            val senhaDigitado = senhaLayout.editText?.text.toString().trim()

            emailLayout.error = null
            senhaLayout.error = null

            if (emailDigitado.isEmpty()) {
                emailLayout.error = "Preencha o email"
                return@setOnClickListener
            }
            if (senhaDigitado.isEmpty()) {
                senhaLayout.error = "Preencha a senha"
                return@setOnClickListener
            }

            continuar.isEnabled = false

            lifecycleScope.launch {
                try {
                    val loginRequest = LoginRequest(
                        email = emailDigitado,
                        senha = senhaDigitado
                    )

                    val response = withContext(Dispatchers.IO) {
                        apiUsuario.loginFuncionario(loginRequest)
                    }

                    if (response.isSuccessful && response.body() != null) {
                        val funcionario = response.body()!!

                        val intent = Intent(this@Login, MainActivity::class.java)
                        intent.putExtra("fragmentToLoad", "HomeFragment")
                        intent.putExtra("token", funcionario.token)
                        intent.putExtra("nome", funcionario.nome)
                        intent.putExtra("cracha", funcionario.numeroCracha)
                        startActivity(intent)
                        finish()
                    } else if (response.code() == 401) {
                        senhaLayout.error = "Email ou senha incorretos"
                    } else {
                        senhaLayout.error = "Erro ao validar funcionário na API"
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@Login, "Erro de conexão com a API", Toast.LENGTH_SHORT).show()
                } finally {
                    continuar.isEnabled = true
                }
            }
        }

        // Primeiro acesso → direciona para a tela de cadastro inicial
        primeiroAcesso.setOnClickListener {
            startActivity(Intent(this, Primeiro_acesso::class.java))
        }

        // Redefinir senha → direciona para a tela de redefinir senha
        redefinirSenha.setOnClickListener {
            startActivity(Intent(this, Redefinir_senha::class.java))
        }
    }
}
