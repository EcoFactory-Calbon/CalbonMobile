package com.example.calbon

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment

class RelatorioFragment : Fragment(R.layout.fragment_relatorio) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnDrawer = view.findViewById<ImageView>(R.id.configuracao)
        btnDrawer.setOnClickListener {
            // Chama o drawer da Activity
            (activity as? MainActivity)?.openDrawerFromFragment()
        }
    }
}
