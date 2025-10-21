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
import com.example.calbon.api.RetrofitClient
import com.example.calbon.model.LinkItem
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
            val position = (0 until adapter.itemCount).firstOrNull { adapter.getItem(it).id == item.id }
            position?.let { adapter.notifyItemChanged(it) }
        }

        recyclerView.adapter = adapter

        fetchSalvos()
    }

    private fun fetchSalvos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
//                val allLinks = RetrofitClient.instance.getLinks() // ou lista local
//                val savedLinks = allLinks.filter { SavedPostsManager.isPostSaved(requireContext(), it.id.toString()) }

                withContext(Dispatchers.Main) {
//                    adapter.setItems(savedLinks)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erro ao carregar links", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun filterSalvos() {
        val currentLinks = adapter.run { (0 until itemCount).map { getItem(it) } }
        val filtered = currentLinks.filter { SavedPostsManager.isPostSaved(requireContext(), it.id.toString()) }
        adapter.setItems(filtered)
    }
}
