package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class InicioAcudiente : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicioacudiente)
        supportActionBar?.hide()

        // 🔹 Referencias de las tarjetas
        val btnTareas = findViewById<ImageView>(R.id.tareas)
        val btnNotas = findViewById<ImageView>(R.id.notas)
        val btnCalendario = findViewById<ImageView>(R.id.calendario)
        val btnMensajes = findViewById<ImageView>(R.id.mensajes)

        // 🔹 Acciones con animación
        setAnimatedClick(btnTareas) {
            startActivity(Intent(this, TareasHijo::class.java))
        }
        setAnimatedClick(btnNotas) {
            startActivity(Intent(this, NotasEst::class.java))
        }
        setAnimatedClick(btnCalendario) {
            startActivity(Intent(this, Calendario::class.java))
        }
        setAnimatedClick(btnMensajes) {
            startActivity(Intent(this, MensajesProf::class.java))
        }

        // 🔹 Barra inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_calendar -> { startActivity(Intent(this, Calendario::class.java)); true }
                R.id.nav_notifications -> { startActivity(Intent(this, Comunicados::class.java)); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivityEst::class.java)); true }
                else -> false
            }
        }
    }

    /**
     * 💫 Animación fluida con rebote y ligera elevación al hacer clic.
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
