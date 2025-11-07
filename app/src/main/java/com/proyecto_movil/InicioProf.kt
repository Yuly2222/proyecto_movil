package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class InicioProf : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicioprof)

        // 🔹 Ocultar ActionBar (para visual limpio)
        supportActionBar?.hide()

        // 🔹 Referencias a los botones (tarjetas)
        val btnComunicados = findViewById<ImageView>(R.id.comunicados)
        val btnCalendario = findViewById<ImageView>(R.id.calendario)
        val btnCursos = findViewById<ImageView>(R.id.cursos)
        val btnRegistrarNotas = findViewById<ImageView>(R.id.notas)
        val btnMensajes = findViewById<ImageView>(R.id.mensajes)
        val btnPerfil = findViewById<ImageView>(R.id.datospersonales)

        // 🔹 Animaciones con navegación
        setAnimatedClick(btnComunicados) {
            startActivity(Intent(this, Comunicados::class.java))
        }
        setAnimatedClick(btnCalendario) {
            startActivity(Intent(this, Calendario_Profe::class.java))
        }
        setAnimatedClick(btnCursos) {
            startActivity(Intent(this, CursosProf::class.java))
        }
        setAnimatedClick(btnRegistrarNotas) {
            startActivity(Intent(this, RegistrarNotasProf::class.java))
        }
        setAnimatedClick(btnMensajes) {
            startActivity(Intent(this, ForoProfesor::class.java))
        }
        setAnimatedClick(btnPerfil) {
            startActivity(Intent(this, ProfileProfe::class.java))
        }

        // 🔹 Configurar barra inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_courses -> { startActivity(Intent(this, CursosProf::class.java)); true }
                R.id.nav_calendar -> { startActivity(Intent(this, Calendario::class.java)); true }
                R.id.nav_notifications -> { startActivity(Intent(this, Comunicados::class.java)); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileProfe::class.java)); true }
                else -> false
            }
        }
    }

    /**
     * 💫 Aplica animación fluida con rebote y ligera elevación al hacer clic.
     * Usada también en InicioEst para mantener coherencia visual.
     */
    private fun setAnimatedClick(view: View, action: () -> Unit) {
        view.setOnClickListener {
            view.animate()
                .scaleX(0.93f)
                .scaleY(0.93f)
                .translationZ(8f)
                .setDuration(80)
                .withEndAction {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .translationZ(0f)
                        .setDuration(80)
                        .withEndAction { action() }
                        .start()
                }
                .start()
        }
    }
}
