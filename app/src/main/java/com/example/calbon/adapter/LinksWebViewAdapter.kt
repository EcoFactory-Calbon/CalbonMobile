package com.example.calbon.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.calbon.R
import com.example.calbon.WebViewActivity
import com.example.calbon.model.Noticia
import com.example.calbon.util.SavedPostsManager

class LinksAdapter(
    private val onSalvoClick: (Noticia) -> Unit
) : RecyclerView.Adapter<LinksAdapter.VH>() {

    private val items = mutableListOf<Noticia>()

    fun setItems(list: List<Noticia>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Noticia = items[position]

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagem: ImageView = itemView.findViewById(R.id.imgThumb)
        private val title: TextView = itemView.findViewById(R.id.tvTitle)
        private val saveIcon: ImageView = itemView.findViewById(R.id.salvo)

        fun bind(item: Noticia) {
            title.text = item.titulo
            val postId = item.titulo // usa o título como ID
            val isSaved = SavedPostsManager.isPostSaved(itemView.context, postId)
            saveIcon.setImageResource(if (isSaved) R.drawable.salvo else R.drawable.salvo_contorno)

            saveIcon.setOnClickListener {
                val newStatus = !SavedPostsManager.isPostSaved(itemView.context, postId)
                SavedPostsManager.savePostStatus(itemView.context, postId, newStatus)
                saveIcon.setImageResource(if (newStatus) R.drawable.salvo else R.drawable.salvo_contorno)
                onSalvoClick(item)
            }

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("url", item.link)
                intent.putExtra("title", item.titulo)
                context.startActivity(intent)
            }

            Glide.with(imagem.context)
                .load(R.drawable.ic_web_placeholder)
                .placeholder(R.drawable.ic_web_placeholder)
                .error(R.drawable.ic_web_error)
                .into(imagem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
}
