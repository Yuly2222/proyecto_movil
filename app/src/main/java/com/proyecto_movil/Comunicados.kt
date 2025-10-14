package com.proyecto_movil

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent

class Comunicados : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imicio_comunicados)

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_calendar

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, InicioEst::class.java))
                    true
                }

                R.id.nav_courses -> {
                    startActivity(Intent(this, NotasEst::class.java))
                    true
                }

                R.id.nav_calendar -> {
                    startActivity(Intent(this, Calendario::class.java))
                    true
                }
                R.id.nav_notifications -> true

                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivityEst::class.java))
                    true
                }

                else -> false
            }
        }

        val rvComunicados = findViewById<RecyclerView>(R.id.rvComunicados)
        rvComunicados.layoutManager = LinearLayoutManager(this)

        val listaComunicados = listOf(
            Comunicado(R.drawable.estrella, "Juan", "10:00 a.m.", "Título 1", "Mensaje 1..."),
            Comunicado(R.drawable.estrella, "Ana", "11:30 a.m.", "Título 2", "Mensaje 2...")
        )

        rvComunicados.adapter = AdapterComunicados(listaComunicados)


    }
}