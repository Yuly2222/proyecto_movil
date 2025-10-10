package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class Calendario : BaseActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: EventoAdapter
    private lateinit var calendarView: CalendarView

    // Simulación de datos
    private val eventos = listOf(
        Evento(LocalDate.of(2025, 10, 6).toEpochDay(), "Exposición Redes", "Edif. B, Aula 203", "08:00"),
        Evento(LocalDate.of(2025, 10, 6).toEpochDay(), "Entrega Parcial", "Aula Virtual", "23:59"),
        Evento(LocalDate.of(2025, 10, 7).toEpochDay(), "Reunión IEEE", "Sala 4", "16:00"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        setContentView(R.layout.bottom_navigation_view)
        setupBottomNav(R.id.nav_calendar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario)

        calendarView = findViewById(R.id.calendarView)
        rv = findViewById(R.id.rvEventos)

        adapter = EventoAdapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // === Tu código de navegación inferior ===
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
        // =======================================

        // 1) Cargar eventos del día mostrado inicialmente
        val initialDay = millisToEpochDay(calendarView.date)
        renderForDay(initialDay)

        // 2) Cambiar al seleccionar fecha
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val day = LocalDate.of(year, month + 1, dayOfMonth).toEpochDay()
            renderForDay(day)
        }
    }

    private fun renderForDay(epochDay: Long) {
        val delDia = eventos.filter { it.fechaEpochDay == epochDay }
        adapter.submit(delDia)
    }

    private fun millisToEpochDay(millis: Long): Long =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
}
