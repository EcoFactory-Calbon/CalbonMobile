package com.example.calbon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class DefinirSenha : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_definir_senha)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Firebase
        auth = FirebaseAuth.getInstance()

        // Variáveis
        val finalizar = findViewById<Button>(R.id.finalizar)
        val senha = findViewById<TextInputLayout>(R.id.InputDefinirSenha)
        val confirmarSenha = findViewById<TextInputLayout>(R.id.InputConfirmarSenha)
        val emailRecebido = intent.getStringExtra("emailUsuario") ?: ""

        // Clique do botão FINALIZAR
        finalizar.setOnClickListener {
            val senhaDigitada = senha.editText?.text.toString()
            val confirmarSenhaDigitada = confirmarSenha.editText?.text.toString()

            if (senhaDigitada.isEmpty() || confirmarSenhaDigitada.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senhaDigitada != confirmarSenhaDigitada) {
                Toast.makeText(this, "Senhas diferentes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Criar usuário no Firebase
            auth.createUserWithEmailAndPassword(emailRecebido, senhaDigitada)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
