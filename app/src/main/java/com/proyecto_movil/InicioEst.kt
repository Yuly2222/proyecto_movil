package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class InicioEst : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicioest)

        // ✅ Inicializar botones
        val btnComunicados = findViewById<ImageView>(R.id.comunicados)
        val btnCalendario = findViewById<ImageView>(R.id.calendario)
        val btnNotas = findViewById<ImageView>(R.id.notas)
        val btnTareas = findViewById<ImageView>(R.id.tareas)
        val btnUsuario = findViewById<ImageView>(R.id.datospersonales)

        // ✅ Asignar animaciones con navegación
        setAnimatedClick(btnComunicados) { startActivity(Intent(this, Comunicados::class.java)) }
        setAnimatedClick(btnCalendario) { startActivity(Intent(this, Calendario::class.java)) }
        setAnimatedClick(btnNotas) { startActivity(Intent(this, NotasEst::class.java)) }
        setAnimatedClick(btnTareas) { startActivity(Intent(this, Tareas::class.java)) }
        setAnimatedClick(btnUsuario) { startActivity(Intent(this, ProfileActivityEst::class.java)) }

        // ✅ Configurar barra inferior
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_courses -> { startActivity(Intent(this, NotasEst::class.java)); true }
                R.id.nav_calendar -> { startActivity(Intent(this, Calendario::class.java)); true }
                R.id.nav_notifications -> { startActivity(Intent(this, Comunicados::class.java)); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivityEst::class.java)); true }
                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * 💫 Aplica animación fluida con rebote y ligera elevación al hacer clic.
     */
    private fun setAnimatedClick(view: View, action: () -> Unit) {
        view.setOnClickListener {
            view.animate()
                .scaleX(0.93f)
                .scaleY(0.93f)
                .translationZ(8f) // agrega sensación de elevación
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
