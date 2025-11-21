package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class Calendario_Profe : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: EventoAdapter
    private lateinit var calendarView: CalendarView
    private lateinit var db: FirebaseDatabase

    // Lista que llenaremos con los eventos de Firebase
    private val eventos = mutableListOf<Evento>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_profe)

        db = FirebaseDatabase.getInstance()

        calendarView = findViewById(R.id.calendarView)
        rv = findViewById(R.id.rvEventos)

        adapter = EventoAdapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // === Navegación inferior ===
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_calendar

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, InicioProf::class.java))
                    true
                }

                R.id.nav_courses -> {
                    startActivity(Intent(this, CursosProf::class.java))
                    true
                }

                R.id.nav_calendar -> true
                R.id.nav_notifications -> {
                    startActivity(Intent(this, Comunicados::class.java))
                    true
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, Comunicados_Profe::class.java))
                    true
                }

                else -> false
            }
        }
        // ===========================

        // 🔥 Cargar eventos desde Firebase
        cargarEventosDeFirebase()

        // 🔥 Cargar eventos del día inicial
        val initialDay = millisToEpochDay(calendarView.date)
        renderForDay(initialDay)

        // 🔥 Cambiar al seleccionar fecha
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val day = LocalDate.of(year, month + 1, dayOfMonth).toEpochDay()
            renderForDay(day)
        }
    }

    private fun cargarEventosDeFirebase() {
        val ref = db.getReference("eventos")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                eventos.clear()

                for (ev in snapshot.children) {
                    val evento = ev.getValue(Evento::class.java)
                    if (evento != null) {
                        eventos.add(evento)
                    }
                }

                // Actualiza los eventos del día seleccionado
                val currentDay = millisToEpochDay(calendarView.date)
                renderForDay(currentDay)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Calendario_Profe, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun renderForDay(epochDay: Long) {
        val delDia = eventos.filter { it.fechaEpochDay == epochDay }
        adapter.submit(delDia)
    }

    private fun millisToEpochDay(millis: Long): Long =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
}
