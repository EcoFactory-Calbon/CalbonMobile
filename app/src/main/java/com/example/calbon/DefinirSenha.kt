package com.example.calbon

import DefinirSenhaRequest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.calbon.api.RetrofitClient.apiUsuario
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefinirSenha : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_definir_senha)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)

        // Recebe os dados do Primeiro_acesso
        val emailUsuario = intent.getStringExtra("email") ?: ""
        val nome = intent.getStringExtra("nome") ?: ""
        val sobrenome = intent.getStringExtra("sobrenome") ?: ""
        val numeroCracha = intent.getIntExtra("numeroCracha", 0)
        val idCargo = intent.getIntExtra("idCargo", 0)
        val idLocalizacao = intent.getIntExtra("idLocalizacao", 0)

        val finalizar = findViewById<Button>(R.id.finalizar)
        val senhaLayout = findViewById<TextInputLayout>(R.id.InputDefinirSenha)
        val confirmarSenhaLayout = findViewById<TextInputLayout>(R.id.InputConfirmarSenha)

        finalizar.setOnClickListener {
            val senha = senhaLayout.editText?.text?.toString()?.trim() ?: ""
            val confirmarSenha = confirmarSenhaLayout.editText?.text?.toString()?.trim() ?: ""

            if (senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mostra progress bar e desabilita botão
            progressBar.visibility = View.VISIBLE
            finalizar.isEnabled = false

            lifecycleScope.launch {
                try {
                    val request = DefinirSenhaRequest(senha)

                    val response = withContext(Dispatchers.IO) {
                        apiUsuario.definirSenha(numeroCracha, request)
                    }

                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@DefinirSenha,
                            "Senha definida com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Cria usuário no Firebase (para autenticação futura)
                        withContext(Dispatchers.IO) {
                            auth.createUserWithEmailAndPassword(emailUsuario, senha)
                        }

                        // Vai para tela de login
                        val intent = Intent(this@DefinirSenha, Login::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(
                            this@DefinirSenha,
                            "Erro ao definir senha: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        android.util.Log.e("API_ERROR", "Erro ${response.code()} - Corpo: $errorBody")
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@DefinirSenha,
                        "Erro de conexão com a API",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    progressBar.visibility = View.GONE
                    finalizar.isEnabled = true
                }
            }
        }
    }
}
