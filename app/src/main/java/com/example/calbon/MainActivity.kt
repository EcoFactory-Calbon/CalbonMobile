package com.example.calbon

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.calbon.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // *** Novo: Atribuir o FAB à CurvedBottomNavigationView ***
        binding.bottomNavigation.fab = binding.fab

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            binding.bottomNavigation.selectedItemId = R.id.home

            binding.bottomNavigation.post {
                binding.bottomNavigation.updateSelectedPosition(0)
                moveFab(0)
                val initialMenuItem = binding.bottomNavigation.menu.findItem(R.id.home)
                updateFabIcon(initialMenuItem)
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val selectedIndex = when (item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    0
                }
                R.id.relatorio -> {
                    replaceFragment(RelatorioFragment())
                    1
                }
                R.id.formulario -> {
                    replaceFragment(FormularioFragment())
                    2
                }
                R.id.perfil -> {
                    replaceFragment(PerfilFragment())
                    3
                }
                else -> 0
            }
            binding.bottomNavigation.updateSelectedPosition(selectedIndex)
            moveFab(selectedIndex)
            updateFabIcon(item)
            true
        }
    }

    private fun moveFab(position: Int) {
        val menuItem = binding.bottomNavigation.menu.getItem(position)
        menuItem?.let {
            val itemView = binding.bottomNavigation.findViewById<View>(it.itemId) ?: return

            if (binding.fab.width == 0) {
                binding.fab.post {
                    moveFab(position)
                }
                return
            }

            val itemViewLocation = IntArray(2)
            itemView.getLocationOnScreen(itemViewLocation)

            val bottomNavLocation = IntArray(2)
            binding.bottomNavigation.getLocationOnScreen(bottomNavLocation)

            val itemRelativeX = (itemViewLocation[0] - bottomNavLocation[0]).toFloat()
            val targetX = itemRelativeX + (itemView.width / 2f) - (binding.fab.width / 2f)

            binding.fab.animate()
                .translationX(targetX)
                .setDuration(300)
                .start()
        }
    }

    private fun updateFabIcon(menuItem: MenuItem?) {
        menuItem?.icon?.let {
            binding.fab.animate().alpha(0f).setDuration(100).withEndAction {
                binding.fab.setImageDrawable(it)
                binding.fab.animate().alpha(1f).setDuration(100).start()
            }.start()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.frameContainer.id, fragment)
            .commit()
    }
}