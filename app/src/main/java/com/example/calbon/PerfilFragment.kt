package com.example.calbon

import android.net.Uri
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

    // Defina as constantes usadas na InfoPessoaisActivity para garantir a consistência
    companion object {
        const val TAG = "PerfilFragment"
        const val PREFS_NAME = "APP_PREFS" // Deve ser o mesmo nome de arquivo de SharedPreferences
        const val IMAGE_URI_KEY = "USER_IMAGE_URI" // Chave usada para salvar a URL do Cloudinary
        const val NUMERO_CRACHA_KEY = "NUMERO_CRACHA" // Chave usada para salvar o crachá
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.imageView)
        nomeTextView = view.findViewById(R.id.nomeUsuario)
        emailTextView = view.findViewById(R.id.emailUsuario)
        progressBar = view.findViewById(R.id.progressBar)

        // 🔹 Carrega imagem de perfil salva (URL do Cloudinary)
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE)

        // CORREÇÃO: Usando IMAGE_URI_KEY (a chave correta)
        val imageUrl = prefs.getString(IMAGE_URI_KEY, null)

        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .transform(CircleCrop())
                .into(imageView)
        } else {
            // Carrega imagem padrão (Ajuste o recurso de imagem padrão se necessário)
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // 🔹 Configura o ViewPager e as abas
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        viewPager.adapter = PerfilPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Posts salvos" else "Configurações"
        }.attach()

        // 🔹 Recupera número do crachá
        numeroCracha = prefs.getInt(NUMERO_CRACHA_KEY, -1)
        Log.d(TAG, "Número do crachá salvo: $numeroCracha")

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

                        // Atualiza as informações de texto
                        nomeTextView.text = "${usuario.nome} ${usuario.sobrenome}" // Assumindo que você quer nome e sobrenome
                        emailTextView.text = usuario.email

                        // Se a URL do usuário vier da API e for diferente da SharedPreferences, carrega a da API.
                        // O código de cima prioriza a SharedPreferences (que tem a URL atualizada pelo usuário)
                        // mas esta chamada aqui garante que a imagem do DB seja exibida se a SharedPreferences estiver vazia.
                        if (usuario.fotoUrl != null) {
                            val prefs = requireActivity().getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE)
                            val currentImageUrl = prefs.getString(IMAGE_URI_KEY, null)

                            // Se a URL da SharedPreferences estiver vazia, ou for diferente, carrega a da API
                            if (currentImageUrl.isNullOrEmpty() || currentImageUrl != usuario.fotoUrl) {
                                Glide.with(this@PerfilFragment)
                                    .load(usuario.fotoUrl)
                                    .transform(CircleCrop())
                                    .into(imageView)
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