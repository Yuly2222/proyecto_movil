package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class NotasEst : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas_est)

        val recyclerView = findViewById<RecyclerView>(R.id.rvNotas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val listaNotas = listOf(
            Nota("01", "Matematicas", "2h. Semana", "3.5"),
            Nota("02", "Ingles", "2h. Semana", "4.0"),
        )
        recyclerView.adapter = NotasAdapter(listaNotas)

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_courses

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, InicioEst::class.java))
                    true
                }

                R.id.nav_courses -> true

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