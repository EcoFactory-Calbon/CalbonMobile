    package com.example.calbon


    import android.os.Bundle
    import android.view.View
    import androidx.appcompat.app.AppCompatActivity
    import androidx.fragment.app.Fragment
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import com.google.android.material.floatingactionbutton.FloatingActionButton

    class MainActivity : AppCompatActivity() {

        private lateinit var bottomNavigation: BottomNavigationView
        private lateinit var fab: FloatingActionButton

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )

            // Pega as views do XML
            bottomNavigation = findViewById(R.id.bottomNavigationView)
            fab = findViewById(R.id.fab)

            // Esconde o FAB (se quiser manter oculto)
            fab.hide()
            fab.isClickable = false

            // Inicializa a navegação
            setupNavigation()

            // Fragment inicial - lógica otimizada
            if (savedInstanceState == null) {
                // Verifica se veio da tela de login com extra específico
                if (intent.getStringExtra("fragmentToLoad") == "Home") {
                    replaceFragment(HomeFragment())
                    bottomNavigation.selectedItemId = R.id.home
                } else {
                    // Carregamento normal
                    replaceFragment(HomeFragment())
                    bottomNavigation.selectedItemId = R.id.home
                }
            }
        }

        private fun setupNavigation() {
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

        private fun replaceFragment(fragment: Fragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }