package com.example.calbon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calbon.adapter.LinksAdapter
import com.example.calbon.retrofit.RetrofitRedisClient
import com.example.calbon.util.SavedPostsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostSalvoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LinksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_post_salvo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.CardsRecycleViewHome)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LinksAdapter { item ->
            // atualiza visual do item clicado
            val position = (0 until adapter.itemCount).firstOrNull { adapter.getItem(it).link == item.link }
            position?.let { adapter.notifyItemChanged(it) }
        }
        recyclerView.adapter = adapter

        fetchSalvos()
    }

    private fun fetchSalvos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Busca todas as notícias do Redis
                val todasNoticias = RetrofitRedisClient.api.listarNoticias()

                // Recupera os posts salvos localmente
                val salvos = SavedPostsManager.getSavedPosts(requireContext())

                // Filtra só os que estão salvos
                val noticiasSalvas = todasNoticias.filter { it.link in salvos }

                withContext(Dispatchers.Main) {
                    adapter.setItems(noticiasSalvas)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro ao carregar posts salvos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
