package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MensajesProf : AppCompatActivity() {

    private lateinit var rvMensajes: RecyclerView
    private lateinit var adapter: MensajeAdapter
    private lateinit var fabNuevo: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensajes_prof)

        rvMensajes = findViewById(R.id.rvMensajes)
        fabNuevo = findViewById(R.id.fabNuevoMensaje)

        // 🔹 Configurar lista de ejemplo
        val listaMensajes = listOf(
            Mensaje("Juan Pérez", "Profesor, tengo duda con la tarea...", "10:30 a.m."),
            Mensaje("María López", "Gracias por la retroalimentación.", "11:05 a.m."),
            Mensaje("Andrés Gómez", "¿Podría revisar mi nota?", "1:45 p.m.")
        )

        rvMensajes.layoutManager = LinearLayoutManager(this)
        adapter = MensajeAdapter(listaMensajes)
        rvMensajes.adapter = adapter

        // 🔹 Acción del botón flotante
        fabNuevo.setOnClickListener {
            Toast.makeText(this, "Redactar nuevo mensaje", Toast.LENGTH_SHORT).show()
        }

        // 🔹 Barra inferior
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_notifications

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, InicioProf::class.java)); true }
                R.id.nav_courses -> { startActivity(Intent(this, cursos_prof::class.java)); true }
                R.id.nav_calendar -> { startActivity(Intent(this, Calendario::class.java)); true }
                R.id.nav_notifications -> true
                R.id.nav_profile -> { startActivity(Intent(this, ProfileProfe::class.java)); true }
                else -> false
            }
        }
    }
}
