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

        auth = FirebaseAuth.getInstance()

        // Recebe os dados do Primeiro_acesso
        val emailUsuario = intent.getStringExtra("emailUsuario") ?: ""
        val nome = intent.getStringExtra("nome") ?: ""
        val sobrenome = intent.getStringExtra("sobrenome") ?: ""
        val numeroCracha = intent.getIntExtra("numeroCracha", 0)
        val idCargo = intent.getIntExtra("idCargo", 0)
        val idLocalizacao = intent.getIntExtra("idLocalizacao", 0)

        val finalizar = findViewById<Button>(R.id.finalizar)
        val senhaLayout = findViewById<TextInputLayout>(R.id.InputDefinirSenha)
        val confirmarSenhaLayout = findViewById<TextInputLayout>(R.id.InputConfirmarSenha)

        finalizar.setOnClickListener {
            val senha = senhaLayout.editText?.text.toString()
            val confirmarSenha = confirmarSenhaLayout.editText?.text.toString()

            if (senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Criar usuário no Firebase com e-mail e senha
            auth.createUserWithEmailAndPassword(emailUsuario, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()

                        // Aqui você pode salvar outros dados do usuário em Firestore ou Realtime Database, se necessário
                        // Ex: nome, sobrenome, crachá, cargo, localização
                        // FirebaseDatabase.getInstance().getReference("usuarios").child(auth.currentUser.uid).setValue(usuario)

                        // Volta para login
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Erro ao cadastrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
