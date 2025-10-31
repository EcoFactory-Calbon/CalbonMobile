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
    private lateinit var progressBar: ProgressBar

    private var numeroCracha: Int = -1

    companion object {
        const val TAG = "PerfilFragment"
        const val PREFS_NAME = "APP_PREFS"
        const val IMAGE_URI_KEY = "USER_IMAGE_URI"
        const val NUMERO_CRACHA_KEY = "NUMERO_CRACHA"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.imageView)
        nomeTextView = view.findViewById(R.id.nomeUsuario)
        emailTextView = view.findViewById(R.id.emailUsuario)
        progressBar = view.findViewById(R.id.progressBar)

        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE)

        // ✅ Tenta carregar imagem do SharedPreferences
        val savedImage = prefs.getString(IMAGE_URI_KEY, null)
        if (!savedImage.isNullOrEmpty()) {
            Glide.with(this)
                .load(savedImage)
                .transform(CircleCrop())
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // ✅ Configuração do ViewPager + Abas
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        viewPager.adapter = PerfilPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Posts salvos" else "Configurações"
        }.attach()

        // ✅ Recupera número do crachá
        numeroCracha = prefs.getInt(NUMERO_CRACHA_KEY, -1)
        Log.d(TAG, "Número do crachá carregado: $numeroCracha")

        if (numeroCracha != -1) {
            buscarDadosUsuario(numeroCracha)
        } else {
            nomeTextView.text = "Nome não disponível"
            emailTextView.text = "Email não disponível"
        }
    }

    private fun buscarDadosUsuario(numeroCracha: Int) {
        progressBar.visibility = View.VISIBLE

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

                        nomeTextView.text = "${usuario.nome} ${usuario.sobrenome}"
                        emailTextView.text = usuario.email

                        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
                        val savedUrl = prefs.getString(IMAGE_URI_KEY, null)

                        // ✅ Se o usuário tiver uma foto no banco
                        if (!usuario.fotoUrl.isNullOrEmpty()) {

                            // ✅ Se SharedPreferences estiver vazio OU divergente, atualiza e carrega
                            if (savedUrl.isNullOrEmpty() || savedUrl != usuario.fotoUrl) {

                                Glide.with(this@PerfilFragment)
                                    .load(usuario.fotoUrl)
                                    .transform(CircleCrop())
                                    .into(imageView)

                                // ✅ Atualiza SharedPreferences
                                prefs.edit()
                                    .putString(IMAGE_URI_KEY, usuario.fotoUrl)
                                    .apply()
                            }
                        }

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
                Log.e(TAG, "Erro: ${e.message}")
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    fun getNumeroCracha(): Int = numeroCracha
}