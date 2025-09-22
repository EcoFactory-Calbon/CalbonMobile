package com.example.calbon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random

class Redefinir_senha : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    // Aqui podemos armazenar o código de 5 dígitos caso queira enviar por SMS
    private var codigoGerado: String = ""

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
        // val InputSmsRedefinirSenha = findViewById<TextInputLayout>(R.id.InputSmsRedefinirSenha) // Para SMS, se quiser usar

        // Função para gerar e enviar o código
        // Comentado porque vamos usar e-mail do Firebase por enquanto
        /*
        fun enviarCodigo(contato: String) {
            // Geração do código aleatório
            val codigo = Random.nextInt(10000, 100000).toString()
            codigoGerado = codigo

            // Enviar por SMS
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("sms:$contato")
                putExtra("sms_body", "O seu código de redefinição de senha é: $codigo")
            }
            startActivity(smsIntent)

            Toast.makeText(this, "Código enviado para $contato", Toast.LENGTH_SHORT).show()
        }
        */

        continuar.setOnClickListener {
            val email = InputEmailRedefinirSenha.editText?.text.toString().trim()
            // val sms = InputSmsRedefinirSenha.editText?.text.toString() // Para SMS, se quiser usar

            // Validação de preenchimento
            if(email.isEmpty()) {
                Toast.makeText(this, "Informe o e-mail para redefinir a senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Se fosse SMS também, verificar se só um campo foi preenchido
            /*
            if(email.isNotEmpty() && sms.isNotEmpty()){
                Toast.makeText(this, "Selecione apenas um contato", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if(email.isEmpty() && sms.isEmpty()){
                Toast.makeText(this, "Selecione um contato", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val contato = if (email.isNotEmpty()) email else sms
            enviarCodigo(contato)
            */

            // Usando Firebase para enviar e-mail de redefinição
            showResetPasswordDialog()
        }
    }

    // Dialogo para enviar e-mail de redefinição via Firebase
    private fun showResetPasswordDialog() {
        val editText = android.widget.EditText(this)
        editText.hint = "Digite seu email"

        AlertDialog.Builder(this)
            .setTitle("Recuperar Senha")
            .setView(editText)
            .setPositiveButton("Enviar") { _, _ ->
                val email = editText.text.toString().trim()
                if (email.isEmpty()) {
                    Toast.makeText(this, "Informe o email", Toast.LENGTH_SHORT).show()
                } else {
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Email de recuperação enviado", Toast.LENGTH_SHORT).show()
                                // Se quiser abrir outra tela, pode usar:
                                // val intent = Intent(this, Verificador::class.java)
                                // intent.putExtra("email", email)
                                // startActivity(intent)
                            } else {
                                Toast.makeText(this, "Erro ao enviar email", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
