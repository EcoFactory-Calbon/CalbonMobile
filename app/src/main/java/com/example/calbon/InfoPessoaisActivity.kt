package com.example.calbon

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InfoPessoaisActivity : AppCompatActivity(), ChangeUsernameDialogListener {
    private fun showChangeDialog(title: String, subtitle: String, field: String) {
        val dialog = ChangeUsernameDialogFragment()
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("subtitle", subtitle)
        bundle.putString("field", field)
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "change$field")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info_pessoais)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nome_completo = findViewById<TextView>(R.id.nome_completo)
        val localizacao = findViewById<TextView>(R.id.localizacao)
        val email = findViewById<TextView>(R.id.email)
        val senha = findViewById<TextView>(R.id.senha)

        val editar_nome = findViewById<ImageView>(R.id.editar_nome)
        val editar_localizazao = findViewById<ImageView>(R.id.editar_localizazao)
        val editar_email = findViewById<ImageView>(R.id.editar_email)
        val editar_senha = findViewById<ImageView>(R.id.editar_senha)
        val voltar = findViewById<ImageView>(R.id.voltar)



        editar_nome.setOnClickListener {
            showChangeDialog("Alterar Nome Completo", nome_completo.text.toString(), "nome_completo")
        }

        editar_localizazao.setOnClickListener {
            showChangeDialog("Alterar Localização", localizacao.text.toString(), "localizacao")
        }

        editar_email.setOnClickListener {
            showChangeDialog("Alterar Email", email.text.toString(), "email")
        }

        editar_senha.setOnClickListener {
            showChangeDialog("Alterar Senha", "Digite a nova senha", "senha")
        }


        voltar.setOnClickListener {
            finish()
        }
    }

    // Aqui recebemos o valor alterado do dialog
    override fun onFieldChanged(field: String, newValue: String) {
        when (field) {
            "nome_completo" -> findViewById<TextView>(R.id.nome_completo).text = newValue
            "localizacao" -> findViewById<TextView>(R.id.localizacao).text = newValue
            "email" -> findViewById<TextView>(R.id.email).text = newValue
            "senha" -> findViewById<TextView>(R.id.senha).text = "********" // máscara
        }
    }
}
