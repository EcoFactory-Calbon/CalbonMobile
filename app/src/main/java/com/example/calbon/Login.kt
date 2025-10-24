package com.example.calbon

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.example.calbon.api.RetrofitClient
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val emailLayout = findViewById<TextInputLayout>(R.id.InputEmail)
        val senhaLayout = findViewById<TextInputLayout>(R.id.InputSenha)
        val voltar = findViewById<ImageView>(R.id.voltarRedefinirSenha)
        val continuar = findViewById<Button>(R.id.continuar)
        val primeiroAcesso = findViewById<TextView>(R.id.primeiroAcesso)
        val redefinirSenha = findViewById<TextView>(R.id.esqueciSenha)

        emailLayout.defaultHintTextColor = ColorStateList.valueOf(Color.WHITE)
        senhaLayout.defaultHintTextColor = ColorStateList.valueOf(Color.WHITE)

        voltar.setOnClickListener { finish() }

        continuar.setOnClickListener {
            val emailDigitado = emailLayout.editText?.text.toString().trim()
            val senhaDigitado = senhaLayout.editText?.text.toString().trim()

            emailLayout.error = null
            senhaLayout.error = null

            if (emailDigitado.isEmpty()) { emailLayout.error = "Preencha o email"; return@setOnClickListener }
            if (senhaDigitado.isEmpty()) { senhaLayout.error = "Preencha a senha"; return@setOnClickListener }

            continuar.isEnabled = false

            lifecycleScope.launch {
                try {
                    val loginRequest = LoginRequest(emailDigitado, senhaDigitado)

                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.getApiUsuarioSemAuth().loginFuncionario(loginRequest)
                    }

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() != null) {
                            val funcionario = response.body()!!

                            // Salva token e crachá em SharedPreferences
                            val prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                            prefs.edit()
                                .putString("TOKEN", funcionario.token)
                                .putInt("NUMERO_CRACHA", funcionario.numeroCracha)
                                .putString("SENHA_REAL", senhaDigitado)
                                .apply()


                            Toast.makeText(
                                this@Login,
                                "Bem-vindo, ${funcionario.nome}!",
                                Toast.LENGTH_LONG
                            ).show()

                            // Vai para MainActivity
                            startActivity(Intent(this@Login, MainActivity::class.java))
                            finish()
                        } else if (response.code() == 401) {
                            senhaLayout.error = "Email ou senha incorretos"
                        } else {
                            senhaLayout.error = "Erro ao validar funcionário na API"
                        }

                        Log.d("API_LOGIN", "Código: ${response.code()}")
                        Log.d("API_LOGIN", "Corpo: ${response.errorBody()?.string()}")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@Login, "Erro de conexão com a API", Toast.LENGTH_SHORT).show()
                } finally {
                    continuar.isEnabled = true
                }
            }
        }

        primeiroAcesso.setOnClickListener {
            startActivity(Intent(this, Primeiro_acesso::class.java))
        }
        redefinirSenha.setOnClickListener {
            startActivity(Intent(this, Redefinir_senha::class.java))
        }
    }
}
