package com.example.calbon.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.calbon.PostSalvoFragment
import com.example.calbon.ConfiguracaoFragment // Certifique-se de que o nome desta classe está correto

class PerfilPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // Lista de instâncias dos Fragmentos que serão exibidos nas abas.
    // O ViewPager usará esta lista para criar as páginas.
    private val fragments = listOf(
        PostSalvoFragment(),      // Posição 0: A aba de Posts Salvos
        ConfiguracaoFragment()    // Posição 1: A aba de Configurações
    )

    /**
     * Retorna o número total de itens (fragmentos/abas) no ViewPager.
     */
    override fun getItemCount(): Int = fragments.size

    /**
     * Cria e retorna o Fragmento na posição solicitada pelo ViewPager.
     */
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}