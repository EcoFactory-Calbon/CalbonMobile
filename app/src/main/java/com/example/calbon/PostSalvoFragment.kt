package com.example.calbon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calbon.adapter.LinksAdapter
import com.example.calbon.model.Noticia
import com.example.calbon.retrofit.RetrofitRedisClient
import com.example.calbon.util.SavedPostsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostSalvoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LinksAdapter
    private var fetchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_post_salvo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuração da RecyclerView
        // ATENÇÃO: Confirme se R.id.CardsRecycleViewHome é o ID correto no fragment_post_salvo.xml
        recyclerView = view.findViewById(R.id.CardsRecycleViewHome)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = LinksAdapter { item ->
            // Lógica para atualizar o item após clique no ícone 'Salvar'/'Dessalvar'
            val position = (0 until adapter.itemCount).firstOrNull {
                adapter.getItem(it).link == item.link
            }
            position?.let {
                adapter.notifyItemChanged(it)

                // Se o post foi dessalvo, ele deve ser removido da lista.
                // Usando 'item.titulo' como chave, que é a identificação do post.
                if (!SavedPostsManager.isPostSaved(requireContext(), item.titulo)) {
                    // Recarrega a lista para remover o post dessalvo
                    fetchSalvos()
                }
            }
        }
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Importante: Recarrega a lista toda vez que o fragmento se torna visível (aba selecionada)
        fetchSalvos()
    }

    private fun fetchSalvos() {
        // Evita iniciar uma nova busca se uma já estiver em andamento
        if (fetchJob?.isActive == true) return

        // Usa o lifecycleScope do fragmento para gerenciar a corotina
        fetchJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Obtém a lista de chaves (títulos) dos posts salvos
                val salvos = SavedPostsManager.getSavedPosts(requireContext())

                if (salvos.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        if (isAdded) {
                            adapter.setItems(emptyList())
                            Toast.makeText(requireContext(), "Nenhum post salvo ainda", Toast.LENGTH_SHORT).show()
                        }
                    }
                    return@launch
                }

                // 2. Busca todas as notícias disponíveis no servidor
                val todasNoticias = RetrofitRedisClient.api.listarNoticias()

                // 3. Filtra as notícias: apenas aquelas cujo TÍTULO (chave) está na lista de salvos
                val noticiasSalvas: List<Noticia> = todasNoticias.filter { it.titulo in salvos }

                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        adapter.setItems(noticiasSalvas)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            "Erro ao carregar posts salvos: ${e.localizedMessage ?: "erro desconhecido"}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancela o job para evitar vazamento de memória e tentativas de atualização de View
        fetchJob?.cancel()
    }
}