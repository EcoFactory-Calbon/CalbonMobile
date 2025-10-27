package com.example.calbon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calbon.R
import com.example.calbon.model.Notificacao
import java.text.SimpleDateFormat
import java.util.*

class NotificacaoAdapter(private val lista: List<Notificacao>) :
    RecyclerView.Adapter<NotificacaoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.titulo)
        val mensagem: TextView = view.findViewById(R.id.mensagem)
        val dataHora: TextView = view.findViewById(R.id.dataHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notificacao, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notif = lista[position]
        holder.titulo.text = notif.titulo
        holder.mensagem.text = notif.mensagem
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.dataHora.text = sdf.format(Date(notif.dataHora))
    }
}
