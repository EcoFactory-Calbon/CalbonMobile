package com.example.calbon

import UserItem
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fab: FloatingActionButton
    private var user: UserItem? = null // Armazena o usuário logado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )

        // Pega o usuário vindo do Login
        user = intent.getParcelableExtra("user")

        // Pega as views do XML
        bottomNavigation = findViewById(R.id.bottomNavigationView)
        fab = findViewById(R.id.fab)

        // Esconde o FAB (se quiser manter oculto)
        fab.hide()
        fab.isClickable = false

        // Inicializa a navegação
        setupNavigation()

        // Fragment inicial
        if (savedInstanceState == null) {
            val fragmentToLoad = intent.getStringExtra("fragmentToLoad")
            when (fragmentToLoad) {
                "HomeFragment" -> replaceFragment(HomeFragment())
                else -> replaceFragment(HomeFragment())
            }
            bottomNavigation.selectedItemId = R.id.home
        }
    }

    private fun setupNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.home -> HomeFragment()
                R.id.relatorio -> RelatorioFragment()
                R.id.formulario -> FormularioFragment()
                R.id.perfil -> {
                    // Passa o usuário logado para o PerfilFragment
                    val perfilFragment = PerfilFragment()
                    val bundle = Bundle()
                    bundle.putParcelable("user", user)
                    perfilFragment.arguments = bundle
                    perfilFragment
                }
                else -> HomeFragment()
            }

            replaceFragment(fragment)
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
