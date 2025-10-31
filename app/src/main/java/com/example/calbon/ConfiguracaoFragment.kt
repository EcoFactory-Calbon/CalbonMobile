package com.example.calbon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.calbon.util.SessionManager

class ConfiguracaoFragment : Fragment() {

    private var numeroCracha: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configuracao, container, false)

        val notificacao = view.findViewById<Button>(R.id.notificacao)
        val infoPerfilButton = view.findViewById<Button>(R.id.infoPerfil)
        val sairButton = view.findViewById<Button>(R.id.sair)

        // Pega o número do crachá diretamente do SessionManager
        numeroCracha = SessionManager.getNumeroCracha(requireContext())

        // Botão de notificações
        notificacao.setOnClickListener {
            val intent = Intent(activity, NotificacoesActivity::class.java)
            startActivity(intent)
        }

        // Botão de informações pessoais
        infoPerfilButton.setOnClickListener {
            val intent = Intent(activity, InfoPessoaisActivity::class.java)
            intent.putExtra("numeroCracha", numeroCracha)
            startActivity(intent)
        }

        // Botão sair
        sairButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Sair da conta")
                .setMessage("Tem certeza que deseja sair?")
                .setPositiveButton("Sim") { _, _ ->
                    // Limpa sessão usando SessionManager
                    SessionManager.logout(requireContext())

                    // Redireciona para a PrimeiraTela (login ou tela inicial)
                    val intent = Intent(activity, PrimeiraTela::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    requireActivity().finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        return view
    }
}
