package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InicioEst : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicioest)

        // Configurar la barra inferior
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home // este marcará como activo el ítem "Perfil"

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_courses -> {
                    startActivity(Intent(this, ItemMateriaNotasActivity::class.java))
                    true
                }
                R.id.nav_calendar -> {
                    startActivity(Intent(this, Calendario::class.java))
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, Comunicados::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivityEst ::class.java))
                    true
                }
                else -> false
            }
        }

        // Ajustes de insets para status bar y nav bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

