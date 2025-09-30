package com.example.calbon.adapter

import com.example.calbon.PostSalvoFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.calbon.ConfiguracaoFragment

class PerfilPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        PostSalvoFragment(),
        ConfiguracaoFragment()
    )


    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}
