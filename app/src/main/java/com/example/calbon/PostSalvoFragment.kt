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
    private var fetchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_post_salvo, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nota: É seguro usar requireContext() aqui, pois onViewCreated só é chamado
        // quando o Fragment está anexado.
        recyclerView = view.findViewById(R.id.CardsRecycleViewHome)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = LinksAdapter { item ->
            val position = (0 until adapter.itemCount).firstOrNull {
                adapter.getItem(it).link == item.link
            }
            position?.let { adapter.notifyItemChanged(it) }
        }
        recyclerView.adapter = adapter

        fetchSalvos()
    }

    private fun fetchSalvos() {
        if (fetchJob?.isActive == true) return

        // Usamos o lifecycleScope.launch ou viewLifecycleOwner.lifecycleScope.launch para melhor
        // controle de ciclo de vida. Mantenho CoroutineScope para consistência, mas o cancelamento
        // no onDestroyView é essencial.
        fetchJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                // É seguro usar requireContext() aqui, pois estamos no Dispatchers.IO
                // e é a primeira chamada do job (Se o Fragment desanexar logo depois, o withContext no final fará a verificação).
                val salvos = SavedPostsManager.getSavedPosts(requireContext())

                if (salvos.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        // --- CORREÇÃO APLICADA AQUI: Adiciona a verificação isAdded ---
                        if (isAdded) {
                            adapter.setItems(emptyList())
                            Toast.makeText(requireContext(), "Nenhum post salvo ainda", Toast.LENGTH_SHORT).show()
                        }
                    }
                    return@launch
                }

                // Busca todas as notícias do Redis
                val todasNoticias = RetrofitRedisClient.api.listarNoticias()

                // Filtra só as que estão salvas
                val noticiasSalvas: List<Noticia> = todasNoticias.filter { it.link in salvos }

                withContext(Dispatchers.Main) {
                    // --- CORREÇÃO APLICADA AQUI: Adiciona a verificação isAdded ---
                    if (isAdded) {
                        if (noticiasSalvas.isEmpty()) {
                            Toast.makeText(requireContext(), "Nenhum post salvo encontrado", Toast.LENGTH_SHORT).show()
                        }
                        adapter.setItems(noticiasSalvas)
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // --- CORREÇÃO APLICADA AQUI: Adiciona a verificação isAdded ---
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            "Erro ao carregar posts salvos: ${e.localizedMessage ?: "erro desconhecido"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // ESSENCIAL: Cancela o Job quando a View é destruída para evitar vazamentos de memória
        // e tentativas de atualizar Views inexistentes.
        fetchJob?.cancel()
    }
}