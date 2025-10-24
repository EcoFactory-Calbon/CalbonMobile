package com.example.calbon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fab: FloatingActionButton
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        bottomNavigation = findViewById(R.id.bottomNavigationView)
        fab = findViewById(R.id.fab)
        navView = findViewById(R.id.nav_view)

        fab.hide()
        fab.isClickable = false

        setupBottomNavigation()
        setupDrawerMenu()

        // Fragment inicial
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            bottomNavigation.selectedItemId = R.id.home
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.home -> HomeFragment()
                R.id.relatorio -> RelatorioFragment()
                R.id.formulario -> FormularioFragment()
                R.id.perfil -> PerfilFragment()
                else -> HomeFragment()
            }
            replaceFragment(fragment)
            true
        }
    }

    private fun setupDrawerMenu() {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_dashboard -> replaceFragment(RelatorioFragment())
                R.id.nav_form -> replaceFragment(FormularioFragment())
                R.id.nav_profile -> replaceFragment(PerfilFragment())
                R.id.nav_terms -> {} // implementar
                R.id.nav_help -> {} // implementar
                R.id.nav_settings -> replaceFragment(ConfiguracaoFragment())
            }
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.END)
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // 🔹 Método para abrir o drawer a partir de um Fragment
    fun openDrawerFromFragment() {
        drawerLayout.openDrawer(androidx.core.view.GravityCompat.END)
    }
}
