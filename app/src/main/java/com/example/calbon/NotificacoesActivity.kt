package com.example.calbon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calbon.adapter.NotificacaoAdapter
import com.example.calbon.utils.NotificationUtils

class NotificacoesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificacoes)

        val recycler = findViewById<RecyclerView>(R.id.recyclerNotificacoes)
        recycler.layoutManager = LinearLayoutManager(this)

        // Pega o número do crachá do usuário logado
        val prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val numeroCracha = prefs.getInt("NUMERO_CRACHA", -1)

        val notificacoes = NotificationUtils.getNotificacoes(this, numeroCracha)
        recycler.adapter = NotificacaoAdapter(notificacoes)
    }
}