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
import kotlin.random.Random // Import necessário para a randomização

class LinksAdapter(
    private val onSalvoClick: (Noticia) -> Unit
) : RecyclerView.Adapter<LinksAdapter.VH>() {

    // 1. LISTA DE IMAGENS DE POSTS (Recursos de Drawable)
    // Os IDs de recurso (Int) que representam seus arquivos em res/drawable/
    private val postImages = intArrayOf(
        R.drawable.img_1,
        R.drawable.img_2,
        R.drawable.img_3,
        R.drawable.img_4,
        R.drawable.img_5,
        R.drawable.img_6,
        R.drawable.img_7,
        R.drawable.img_8,
        R.drawable.img_9,
        R.drawable.img_10,
        R.drawable.img_11,
        R.drawable.img_12,
        R.drawable.img_13,
        R.drawable.img_14,
        R.drawable.img_15,
        R.drawable.img_16,
        R.drawable.img_17,
        R.drawable.img_18,
        R.drawable.img_19,
        R.drawable.img_20,
        R.drawable.img_21,
        R.drawable.img_22,
        R.drawable.img_24,
        R.drawable.img_25,
        R.drawable.img_26,
        R.drawable.img_27,
        R.drawable.img_28,
        R.drawable.img_29,
        R.drawable.img_30
    )

    private val items = mutableListOf<Noticia>()

    fun setItems(list: List<Noticia>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Noticia = items[position]

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ATENÇÃO: O ID para a imagem do card é R.id.imgThumb
        private val imagem: ImageView = itemView.findViewById(R.id.imgThumb)
        private val title: TextView = itemView.findViewById(R.id.tvTitle)
        private val saveIcon: ImageView = itemView.findViewById(R.id.salvo)

        // O método bind não será mais responsável pela imagem, faremos isso no onBindViewHolder.
        fun bind(item: Noticia, randomDrawableId: Int) {
            title.text = item.titulo
            val postId = item.titulo
            val isSaved = SavedPostsManager.isPostSaved(itemView.context, postId)

            // 3. CARREGA A IMAGEM RANDOMIZADA USANDO GLIDE
            // Isso substitui o carregamento estático que estava usando `ic_web_placeholder`.
            Glide.with(imagem.context)
                .load(randomDrawableId) // Carrega o ID de recurso aleatório
                .placeholder(R.drawable.ic_web_placeholder)
                .error(R.drawable.ic_web_error)
                .into(imagem)

            // Lógica do ícone Salvar
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
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        return VH(view)
    }

    // 2. IMPLEMENTAÇÃO DA RANDOMIZAÇÃO AQUI
    override fun onBindViewHolder(holder: VH, position: Int) {
        // Escolhe um ID de recurso aleatório da lista 'postImages'
        val randomDrawableId = postImages[Random.nextInt(postImages.size)]

        // Passa o ID de recurso aleatório para o método bind
        holder.bind(items[position], randomDrawableId)
    }

    override fun getItemCount() = items.size
}