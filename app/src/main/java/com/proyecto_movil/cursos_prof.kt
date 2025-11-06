package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class cursos_prof : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cursos_prof)

        // 📘 RecyclerView de cursos
        val recyclerView = findViewById<RecyclerView>(R.id.rvCursos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Lista de ejemplo
        val listaCursos = listOf(
            Curso("Programación I", "INF-101", "Lunes y Miércoles, 8:00 - 10:00"),
            Curso("Estructuras de Datos", "INF-202", "Martes y Jueves, 10:00 - 12:00"),
            Curso("Bases de Datos", "INF-303", "Viernes, 9:00 - 12:00")
        )

        recyclerView.adapter = CursoAdapter(listaCursos)

        // 🔹 Barra de navegación inferior
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_courses

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, InicioProf::class.java))
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
                    startActivity(Intent(this, cursos_prof::class.java))
                    true
                }

                else -> false
            }
        }
    }
}
