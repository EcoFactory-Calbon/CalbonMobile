package com.example.calbon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calbon.R
import com.example.calbon.model.Noticia

class NoticiasAdapter(
    private val listaNoticias: List<Noticia>
) : RecyclerView.Adapter<NoticiasAdapter.NoticiaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post, parent, false)
        return NoticiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticiaViewHolder, position: Int) {
        val noticia = listaNoticias[position]
        holder.tituloTextView.text = noticia.titulo

        // Placeholder temporário até integrarmos o randomizador de imagens
        holder.imagemNoticia.setImageResource(R.drawable.icone_ajuda_suporte)
    }

    override fun getItemCount(): Int = listaNoticias.size

    class NoticiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.tvTitle)
        val imagemNoticia: ImageView = itemView.findViewById(R.id.imgThumb)
    }
}
