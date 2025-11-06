package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class RegistrarNotasProf : AppCompatActivity() {

    private lateinit var spinnerCursos: Spinner
    private lateinit var rvEstudiantes: RecyclerView
    private lateinit var btnGuardarNotas: Button
    private lateinit var adapter: EstudianteNotaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_notas_prof)
        supportActionBar?.hide()

        // 🔹 Inicializar vistas
        spinnerCursos = findViewById(R.id.spinnerCursos)
        rvEstudiantes = findViewById(R.id.rvEstudiantes)
        btnGuardarNotas = findViewById(R.id.btnGuardarNotas)

        // 🔹 Lista de cursos (simulada)
        val cursos = listOf("Matemáticas 9°A", "Física 10°B", "Química 11°A")
        spinnerCursos.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cursos)

        // 🔹 Lista de estudiantes (simulada)
        val estudiantes = mutableListOf(
            EstudianteNota("Juan Pérez", 4.2),
            EstudianteNota("María López", 3.8),
            EstudianteNota("Andrés Gómez", 4.5),
            EstudianteNota("Sofía Torres", 4.0)
        )

        rvEstudiantes.layoutManager = LinearLayoutManager(this)
        adapter = EstudianteNotaAdapter(estudiantes)
        rvEstudiantes.adapter = adapter

        // 🔹 Guardar notas
        btnGuardarNotas.setOnClickListener {
            Toast.makeText(this, "Notas guardadas correctamente ✅", Toast.LENGTH_SHORT).show()
        }

        // 🔹 Barra inferior de navegación
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_courses
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, InicioProf::class.java)); true }
                R.id.nav_courses -> true
                R.id.nav_calendar -> { startActivity(Intent(this, Calendario::class.java)); true }
                R.id.nav_notifications -> { startActivity(Intent(this, Comunicados::class.java)); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileProfe::class.java)); true }
                else -> false
            }
        }
    }
}
