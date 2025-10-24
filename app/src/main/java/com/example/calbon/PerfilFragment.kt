package com.example.calbon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.calbon.adapter.PerfilPagerAdapter
import com.example.calbon.api.RetrofitClient
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilFragment : Fragment() {

    private lateinit var nomeTextView: TextView
    private lateinit var emailTextView: TextView
    private var numeroCracha: Int = -1 // armazenar o crachá

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nomeTextView = view.findViewById(R.id.nomeUsuario)
        emailTextView = view.findViewById(R.id.emailUsuario)

        // Configura ViewPager2 e TabLayout
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        viewPager.adapter = PerfilPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Posts salvos" else "Configurações"
        }.attach()

        val prefs = requireActivity().getSharedPreferences("APP_PREFS", AppCompatActivity.MODE_PRIVATE)
        numeroCracha = prefs.getInt("NUMERO_CRACHA", -1)
        Log.d("PERFIL_FRAGMENT", "Número do crachá salvo: $numeroCracha")

        if (numeroCracha != -1) {
            buscarDadosUsuario(numeroCracha)
        } else {
            nomeTextView.text = "Nome não disponível"
            emailTextView.text = "Email não disponível"
        }
    }

    private fun buscarDadosUsuario(numeroCracha: Int) {
        val api = RetrofitClient.getApiUsuario(requireContext())
        lifecycleScope.launch {
            try {
                val resposta = withContext(Dispatchers.IO) {
                    api.buscarPorCracha(numeroCracha)
                }

                if (resposta.isSuccessful) {
                    val usuarios = resposta.body()
                    if (!usuarios.isNullOrEmpty()) {
                        val usuario = usuarios.first()
                        nomeTextView.text = usuario.nome
                        emailTextView.text = usuario.email
                    } else {
                        nomeTextView.text = "Usuário não encontrado"
                        emailTextView.text = "Usuário não encontrado"
                    }
                } else {
                    nomeTextView.text = "Erro ao carregar"
                    emailTextView.text = "Erro ao carregar"
                }
            } catch (e: Exception) {
                nomeTextView.text = "Erro de conexão"
                emailTextView.text = "Erro de conexão"
            }
        }
    }

    // Função para fornecer o número do crachá para os fragments do ViewPager
    fun getNumeroCracha(): Int = numeroCracha
}
