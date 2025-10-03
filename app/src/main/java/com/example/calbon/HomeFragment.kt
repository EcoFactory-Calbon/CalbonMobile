package com.example.calbon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calbon.adapter.LinksAdapter
import com.example.calbon.api.RetrofitClient
import com.example.calbon.model.LinkItem
import kotlinx.coroutines.CoroutineScope
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

        // RecyclerView
        recyclerView = view.findViewById(R.id.CardsRecycleViewHome)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LinksAdapter { item ->
            val pos = (0 until adapter.itemCount).firstOrNull { adapter.getItem(it).id == item.id }
            pos?.let { adapter.notifyItemChanged(it) }
        }
        recyclerView.adapter = adapter

        fetchLinks()

        // Configuração para abrir drawer
        val configuracao: ImageView = view.findViewById(R.id.configuracao)
        configuracao.setOnClickListener {
            (activity as? MainActivity)?.openDrawerFromFragment()
        }
    }

    private fun fetchLinks() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val links: List<LinkItem> = RetrofitClient.apiNoticias.getLinks()
                withContext(Dispatchers.Main) { adapter.setItems(links) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Erro ao carregar links: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

