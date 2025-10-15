package com.example.calbon

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class FormularioFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar a view e armazenar em uma variável
        val view = inflater.inflate(R.layout.fragment_formulario, container, false)

        // Acessar o botão usando a view inflada
        val botao = view.findViewById<Button>(R.id.buttom)
        botao.setOnClickListener {
            val intent = Intent(activity, PerguntasActivity::class.java)
            startActivity(intent)
        }

        // Retornar a view inflada
        return view
    }
}
