package com.example.calbon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.calbon.adapter.PerfilPagerAdapter
import com.example.calbon.api.RetrofitClient
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilFragment : Fragment() {

    private lateinit var imageView: ShapeableImageView
    private lateinit var nomeTextView: TextView
    private lateinit var emailTextView: TextView
    private var numeroCracha: Int = -1
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_perfil, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.imageView)
        nomeTextView = view.findViewById(R.id.nomeUsuario)
        emailTextView = view.findViewById(R.id.emailUsuario)
        progressBar = view.findViewById(R.id.progressBar)

        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        viewPager.adapter = PerfilPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Posts salvos" else "Configurações"
        }.attach()

        // Carrega imagem do SharedPreferences
        val prefs = requireActivity().getSharedPreferences("APP_PREFS", AppCompatActivity.MODE_PRIVATE)
        prefs.getString(InfoPessoaisActivity.IMAGE_URI_KEY, null)?.let { uriString ->
            Glide.with(this)
                .load(uriString)
                .transform(CircleCrop())
                .into(imageView)
        }

        numeroCracha = prefs.getInt("NUMERO_CRACHA", -1)
        if (numeroCracha != -1) buscarDadosUsuario(numeroCracha)
    }

    private fun buscarDadosUsuario(numeroCracha: Int) {
        progressBar.visibility = View.VISIBLE
        val api = RetrofitClient.getApiUsuario(requireContext())
        lifecycleScope.launch {
            try {
                val resposta = withContext(Dispatchers.IO) { api.buscarPorCracha(numeroCracha) }
                if (resposta.isSuccessful) {
                    val usuario = resposta.body()?.firstOrNull()
                    usuario?.let {
                        nomeTextView.text = it.nome
                        emailTextView.text = it.email
                        it.fotoUrl?.let { url ->
                            Glide.with(this@PerfilFragment)
                                .load(url)
                                .transform(CircleCrop())
                                .into(imageView)
                        }
                    }
                } else {
                    nomeTextView.text = "Erro"
                    emailTextView.text = "Erro"
                }
            } catch (e: Exception) {
                nomeTextView.text = "Erro"
                emailTextView.text = "Erro"
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
