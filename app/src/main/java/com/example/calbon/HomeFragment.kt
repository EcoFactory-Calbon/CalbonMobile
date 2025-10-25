package com.example.calbon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope // Importação correta
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calbon.adapter.LinksAdapter
import com.example.calbon.retrofit.RetrofitRedisClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LinksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Configuração da única RecyclerView (CardsRecycleViewHome)
        recyclerView = view.findViewById(R.id.CardsRecycleViewHome)
        // Usar context dentro de onViewCreated é seguro
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LinksAdapter { item ->
            val pos = (0 until adapter.itemCount).firstOrNull { adapter.getItem(it).link == item.link }
            pos?.let { adapter.notifyItemChanged(it) }
        }
        recyclerView.adapter = adapter

        // 2. Chama a única função de busca necessária
        fetchNoticias()

        // Configuração para abrir drawer
        val configuracao: ImageView = view.findViewById(R.id.configuracao)
        configuracao.setOnClickListener {
            (activity as? MainActivity)?.openDrawerFromFragment()
        }
    }

    private fun fetchNoticias() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val listaNoticias = RetrofitRedisClient.api.listarNoticias()

                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        adapter.setItems(listaNoticias)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                   if (isAdded) {
                        Toast.makeText(context, "Erro ao carregar notícias: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    e.printStackTrace()
                }
            }
        }
    }

}