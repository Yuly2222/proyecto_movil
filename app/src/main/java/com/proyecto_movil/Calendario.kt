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

class Calendario : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: EventoAdapter
    private lateinit var calendarView: CalendarView
    private lateinit var db: DatabaseReference

    private val eventos = mutableListOf<Evento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        calendarView = findViewById(R.id.calendarView)
        rv = findViewById(R.id.rvEventos)

        adapter = EventoAdapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // Inicializar referencia a Firebase
        db = FirebaseDatabase.getInstance().getReference("eventos")

        // === Navegación inferior ===
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
                R.id.nav_calendar -> true
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
        // ===========================

        // Cargar los eventos desde Firebase
        cargarEventosDesdeFirebase()

        // Render inicial según la fecha actual
        val initialDay = millisToEpochDay(calendarView.date)
        renderForDay(initialDay)

        // Cambiar eventos al seleccionar otra fecha
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val day = LocalDate.of(year, month + 1, dayOfMonth).toEpochDay()
            renderForDay(day)
        }
    }

    private fun cargarEventosDesdeFirebase() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventos.clear()
                for (eventoSnap in snapshot.children) {
                    val titulo = eventoSnap.child("titulo").getValue(String::class.java) ?: ""
                    val detalle = eventoSnap.child("detalle").getValue(String::class.java) ?: ""
                    val hora = eventoSnap.child("hora").getValue(String::class.java) ?: ""
                    val fechaEpochDay = eventoSnap.child("fechaEpochDay").getValue(Long::class.java) ?: 0L

                    // Solo agregar si tiene título (para evitar nulos vacíos)
                    if (titulo.isNotEmpty()) {
                        eventos.add(Evento(fechaEpochDay, titulo, detalle, hora))
                    }
                }

                // Actualizar la vista del día actual
                renderForDay(millisToEpochDay(calendarView.date))
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Calendario, "Error al cargar eventos: ${error.message}", Toast.LENGTH_SHORT).show()
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
