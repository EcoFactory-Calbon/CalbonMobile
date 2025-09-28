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

        continuar.setOnClickListener {
            // Transformando o email em MAIÚSCULAS
            val emailDigitado = emailLayout.editText?.text.toString().trim().uppercase()
            val numCrachaDigitado = numCrachaLayout.editText?.text.toString().trim()
            val codigoDigitado = codigoEmpresaLayout.editText?.text.toString().trim()

            // Limpa erros anteriores
            emailLayout.error = null
            numCrachaLayout.error = null
            codigoEmpresaLayout.error = null

            if (emailDigitado.isEmpty() || numCrachaDigitado.isEmpty() || codigoDigitado.isEmpty()) {
                if (emailDigitado.isEmpty()) emailLayout.error = "Preencha o email"
                if (numCrachaDigitado.isEmpty()) numCrachaLayout.error = "Preencha o número do crachá"
                if (codigoDigitado.isEmpty()) codigoEmpresaLayout.error = "Preencha o código da empresa"
                return@setOnClickListener
            }

            continuar.isEnabled = false

            lifecycleScope.launch {
                try {
                    // 1️⃣ Verifica se o email existe na base interna (ignora maiúsculas/minúsculas)
                    val usuarios = withContext(Dispatchers.IO) { apiUsuario.getUsers() }
                    val usuario = usuarios.find { it.email.equals(emailDigitado, ignoreCase = true) }

                    if (usuario == null) {
                        emailLayout.error = "Email não encontrado"
                        continuar.isEnabled = true
                        return@launch
                    }

                    // 2️⃣ Verifica se o email já existe no Firebase
                    val signInMethods = auth.fetchSignInMethodsForEmail(emailDigitado).await()
                    if (!signInMethods.signInMethods.isNullOrEmpty()) {
                        emailLayout.error = "Email já registrado no Firebase"
                        continuar.isEnabled = true
                        return@launch
                    }

                    // 3️⃣ Valida crachá e código
                    var erro = false
                    if (usuario.num_cracha.toString() != numCrachaDigitado) {
                        numCrachaLayout.error = "Número do crachá incorreto"
                        erro = true
                    }
                    if (usuario.codigo_empresa.toString() != codigoDigitado) {
                        codigoEmpresaLayout.error = "Código da empresa incorreto"
                        erro = true
                    }
                    if (erro) {
                        continuar.isEnabled = true
                        return@launch
                    }

                    // 4️⃣ Tudo certo → vai para DefinirSenha
                    val intent = Intent(this@Primeiro_acesso, DefinirSenha::class.java)
                    intent.putExtra("emailUsuario", emailDigitado)
                    startActivity(intent)
                    finish()

                } catch (e: Exception) {
                    e.printStackTrace()
                    emailLayout.error = "Erro ao consultar Firebase ou API"
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
