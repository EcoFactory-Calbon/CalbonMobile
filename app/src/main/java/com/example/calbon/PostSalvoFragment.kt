import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calbon.R
import com.example.calbon.adapter.LinksAdapter
import com.example.calbon.model.LinkItem

class PostSalvoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LinksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_salvo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.CardsRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LinksAdapter()
        recyclerView.adapter = adapter

        fetchLinks()
    }

    private fun fetchLinks() {
        val links = listOf(
            LinkItem(1, "Globo", "https://www.globo.com"),
            LinkItem(2, "G1 Notícias", "https://g1.globo.com"),
            LinkItem(3, "BBC News Brasil", "https://www.bbc.com/portuguese"),
            LinkItem(4, "CNN Brasil", "https://www.cnnbrasil.com.br")

        )
        adapter.setItems(links)
    }
}
