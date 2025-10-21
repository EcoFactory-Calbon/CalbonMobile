package com.example.calbon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calbon.api.PrimeiroAcessoRequest
import com.example.calbon.api.RetrofitClient.apiUsuario
import com.google.android.material.textfield.TextInputLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val emailLayout = findViewById<TextInputLayout>(R.id.InputEmail)
        val numCrachaLayout = findViewById<TextInputLayout>(R.id.InputNumCracha)
        val codigoEmpresaLayout = findViewById<TextInputLayout>(R.id.InputCódigoEmpresa)
        val continuar = findViewById<Button>(R.id.continuar)
        val login = findViewById<TextView>(R.id.login)

        voltar.setOnClickListener { finish() }

        continuar.setOnClickListener {
            val email = emailLayout.editText?.text.toString().trim()
            val crachaText = numCrachaLayout.editText?.text.toString().trim()
            val codigoEmpresaText = codigoEmpresaLayout.editText?.text.toString().trim()

            // Validação de preenchimento
            var erro = false
            if (email.isEmpty()) {
                emailLayout.error = "Informe o e-mail"
                erro = true
            }
            if (crachaText.isEmpty()) {
                numCrachaLayout.error = "Informe o número do crachá"
                erro = true
            }
            if (codigoEmpresaText.isEmpty()) {
                codigoEmpresaLayout.error = "Informe o código da empresa"
                erro = true
            }
            if (erro) return@setOnClickListener

            val cracha = crachaText.toIntOrNull()
            val codigoEmpresa = codigoEmpresaText.toIntOrNull()
            if (cracha == null || codigoEmpresa == null) {
                Toast.makeText(this, "Número do crachá ou código inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            continuar.isEnabled = false

            // Chamada à API para validar primeiro acesso
            lifecycleScope.launch {
                try {
                    val request = PrimeiroAcessoRequest(
                        email = email,
                        numeroCracha = cracha,
                        codigoEmpresa = codigoEmpresa
                    )

                    val response = withContext(Dispatchers.IO) {
                        apiUsuario.primeiroAcesso(request)
                    }

                    if (response.isSuccessful && response.body() != null) {
                        val usuarioValido = response.body()!!

                        // Navega para DefinirSenha, passando os dados do funcionário
                        val intent = Intent(this@Primeiro_acesso, DefinirSenha::class.java)
                        intent.putExtra("email", usuarioValido.email)
                        intent.putExtra("numeroCracha", usuarioValido.numeroCracha)
                        intent.putExtra("nome", usuarioValido.nome)
                        intent.putExtra("sobrenome", usuarioValido.sobrenome)
                        intent.putExtra("idCargo", usuarioValido.id_Cargo)
                        intent.putExtra("idLocalizacao", usuarioValido.id_Localizacao)
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(
                            this@Primeiro_acesso,
                            "Dados inválidos ou funcionário não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@Primeiro_acesso,
                        "Erro ao consultar a API",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    continuar.isEnabled = true
                }
            }
        }

        login.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}
