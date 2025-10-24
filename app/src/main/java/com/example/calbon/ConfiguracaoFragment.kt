package com.example.calbon

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class ConfiguracaoFragment : Fragment() {

    private var numeroCracha: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configuracao, container, false)

        // Recebe o número do crachá do PerfilFragment
        val parentFragment = parentFragment as? PerfilFragment
        numeroCracha = parentFragment?.getNumeroCracha() ?: -1

        val infoPerfilButton = view.findViewById<Button>(R.id.infoPerfil)
        infoPerfilButton.setOnClickListener {
            val intent = Intent(activity, InfoPessoaisActivity::class.java)
            intent.putExtra("numeroCracha", numeroCracha)
            startActivity(intent)
        }

        return view
    }
}
