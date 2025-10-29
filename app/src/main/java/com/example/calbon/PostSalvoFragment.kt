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
import com.example.calbon.model.Noticia
import com.example.calbon.retrofit.RetrofitRedisClient
import com.example.calbon.util.SavedPostsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostSalvoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LinksAdapter
    private var fetchJob: Job? = null // evita chamadas duplicadas

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_post_salvo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.CardsRecycleViewHome)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LinksAdapter { item ->
            // Atualiza visual do item clicado
            val position = (0 until adapter.itemCount).firstOrNull {
                adapter.getItem(it).link == item.link
            }
            position?.let { adapter.notifyItemChanged(it) }
        }
        recyclerView.adapter = adapter

        fetchSalvos()
    }

    private fun fetchSalvos() {
        // Evita iniciar uma nova busca se já houver uma rodando
        if (fetchJob?.isActive == true) return

        fetchJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                // Recupera os links salvos localmente
                val salvos = SavedPostsManager.getSavedPosts(requireContext())

                if (salvos.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        adapter.setItems(emptyList())
                        Toast.makeText(requireContext(), "Nenhum post salvo ainda", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Busca todas as notícias do Redis
                val todasNoticias = RetrofitRedisClient.api.listarNoticias()

                // Filtra só as que estão salvas
                val noticiasSalvas: List<Noticia> = todasNoticias.filter { it.link in salvos }

                withContext(Dispatchers.Main) {
                    if (noticiasSalvas.isEmpty()) {
                        Toast.makeText(requireContext(), "Nenhum post salvo encontrado", Toast.LENGTH_SHORT).show()
                    }
                    adapter.setItems(noticiasSalvas)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Erro ao carregar posts salvos: ${e.localizedMessage ?: "erro desconhecido"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fetchJob?.cancel()
    }
}
