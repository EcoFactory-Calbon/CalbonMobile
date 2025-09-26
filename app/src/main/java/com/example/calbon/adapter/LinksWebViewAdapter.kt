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
import com.example.calbon.model.LinkItem
import java.net.URLEncoder

class LinksAdapter : RecyclerView.Adapter<LinksAdapter.VH>() {

    private val items = mutableListOf<LinkItem>()

    fun setItems(list: List<LinkItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val img: ImageView = itemView.findViewById(R.id.imgThumb)
        private val title: TextView = itemView.findViewById(R.id.tvTitle)

        fun bind(item: LinkItem) {
            title.text = item.title  // corrigi aqui

            // Gerar URL da thumbnail
            val encodedUrl = URLEncoder.encode(item.url, "UTF-8")
            val thumbUrl = "https://s.wordpress.com/mshots/v1/$encodedUrl"

            Glide.with(img.context)
                .load(thumbUrl)
                .placeholder(android.R.drawable.ic_menu_report_image) // drawable padrão
                .error(android.R.drawable.ic_menu_report_image)       // drawable padrão
                .into(img)

            // Clique abre a página completa
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("url", item.url)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
}
