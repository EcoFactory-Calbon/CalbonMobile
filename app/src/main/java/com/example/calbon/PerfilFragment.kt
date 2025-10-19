package com.example.calbon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.calbon.adapter.PerfilPagerAdapter
import com.example.calbon.api.RetrofitClient
import com.example.calbon.api.UsuarioApi
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilFragment : Fragment() {

    private lateinit var nomeTextView: TextView
    private lateinit var emailTextView: TextView

    // Aqui você pode pegar o token salvo do SharedPreferences ou da Activity
    private val token: String?
        get() = activity?.intent?.getStringExtra("token") // ajustar conforme como você armazenou

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        nomeTextView = view.findViewById(R.id.nomeUsuario)
        emailTextView = view.findViewById(R.id.emailUsuario)

        // Adapter do ViewPager2
        val adapter = PerfilPagerAdapter(this)
        viewPager.adapter = adapter

        // Ligando TabLayout e ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Posts salvos"
                1 -> "Configurações"
                else -> ""
            }
        }.attach()

        // Buscar dados do usuário
        buscarDadosUsuario()
    }

    private fun buscarDadosUsuario() {
        val numeroCracha = activity?.intent?.getIntExtra("numeroCracha", -1) ?: -1
        if (numeroCracha == -1) {
            nomeTextView.text = "Nome não disponível"
            emailTextView.text = "Email não disponível"
            return
        }

        lifecycleScope.launch {
            try {
                val resposta = withContext(Dispatchers.IO) {
                    RetrofitClient.apiUsuario.buscarPorCracha(numeroCracha)
                }

                if (resposta.isSuccessful) {
                    val usuario = resposta.body()
                    usuario?.let {
                        nomeTextView.text = it.nome
                        emailTextView.text = it.email
                    }
                } else {
                    nomeTextView.text = "Erro ao carregar"
                    emailTextView.text = "Erro ao carregar"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                nomeTextView.text = "Erro de conexão"
                emailTextView.text = "Erro de conexão"
            }
        }
    }
}
