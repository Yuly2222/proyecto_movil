package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_navigation_view)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Listener de clicks en la barra inferior
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Ya estás en Home, no hace falta abrir otra Activity
                    true
                }

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
                    startActivity(Intent(this, ProfileActivityEst::class.java))
                    true
                }
                else -> false
            }
        }
    }
}

