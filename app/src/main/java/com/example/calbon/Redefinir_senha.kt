package com.example.calbon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class Redefinir_senha : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_redefinir_senha)

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val continuar = findViewById<Button>(R.id.continuar)
        val InputEmailRedefinirSenha = findViewById<TextInputLayout>(R.id.InputEmailRedefinirSenha)

        continuar.setOnClickListener {
            val email = InputEmailRedefinirSenha.editText?.text.toString().trim()

            // Validação de preenchimento
            if (email.isEmpty()) {
                Toast.makeText(this, "Informe o e-mail para redefinir a senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Usando o Firebase para enviar o e-mail de redefinição
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Se o e-mail foi enviado com sucesso
                        Toast.makeText(this, "E-mail de recuperação enviado! Verifique sua caixa de entrada.", Toast.LENGTH_LONG).show()

                        // Navegar para outra tela após o envio
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    } else {
                        // Se houve um erro no envio
                        Toast.makeText(this, "Erro ao enviar e-mail. Verifique o endereço e tente novamente.", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}