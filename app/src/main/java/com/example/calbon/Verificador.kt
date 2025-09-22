package com.example.calbon

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.chaos.view.PinView

class Verificador : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verificador)

        // Ajusta o padding para os systemBars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referência para o PinView
        val pinView = findViewById<PinView>(R.id.pinView)
        val mensagem = findViewById<TextView>(R.id.Mensagem)


        // Captura o PIN digitado
        pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val code = s.toString()
                if (code.length == 5) { // quando todos os dígitos forem preenchidos
                    Toast.makeText(this@Verificador, "Código digitado: $code", Toast.LENGTH_SHORT).show()
                    // aqui você pode chamar a API para validar o código
                }
            }
        })

        val contato = intent.getStringExtra("contato")
        mensagem.text = "Um código de validação foi enviado no contato $contato"

    }
}
