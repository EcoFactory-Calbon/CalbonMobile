package com.example.calbon

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class ConfiguracaoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 1. Infla o layout e armazena a View resultante na variável 'view'
        val view = inflater.inflate(R.layout.fragment_configuracao, container, false)

        val sairButton = view.findViewById<Button>(R.id.sair)

        val ajudaButton = view.findViewById<Button>(R.id.ajuda)

        val termosButton = view.findViewById<Button>(R.id.termos)

        val permisoesButton = view.findViewById<Button>(R.id.permisoes)

        val notificacaoButton = view.findViewById<Button>(R.id.notificacao)

        val infoPerfilButton = view.findViewById<Button>(R.id.infoPerfil)


        infoPerfilButton.setOnClickListener{
            val Intent = Intent(activity, InfoPessoaisActivity::class.java)
            startActivity(Intent)
        }


        sairButton.setOnClickListener {
            // AQUI vai a lógica para sair, deslogar ou chamar o pop-up, etc.
            // Por exemplo: requireActivity().finish()
        }

        // 3. Retorna a View para que o Fragment possa exibi-la
        return view
    }
}