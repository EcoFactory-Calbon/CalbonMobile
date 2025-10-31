package com.example.calbon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class FormularioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_formulario, container, false)

        val botao = view.findViewById<Button>(R.id.buttom)

        botao.setOnClickListener {

            // ✅ Carrega o SharedPreferences
            val prefs = requireActivity().getSharedPreferences("formulario_prefs", Context.MODE_PRIVATE)

            // ✅ Última vez que respondeu
            val ultimaResposta = prefs.getLong("ultima_resposta", 0L)

            // ✅ Momento atual
            val agora = System.currentTimeMillis()

            // ✅ 30 dias em milissegundos
            val dias30 = 30L * 24 * 60 * 60 * 1000

            // ✅ Verifica se ainda está no prazo
            if (ultimaResposta != 0L && (agora - ultimaResposta) < dias30) {
                Toast.makeText(requireContext(), "Você já respondeu o formulário deste mês.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // ✅ Abre a activity normalmente se estiver liberado
            val intent = Intent(activity, PerguntasActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
