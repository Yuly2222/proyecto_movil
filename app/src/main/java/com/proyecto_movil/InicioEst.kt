package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InicioEst : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicioest)

        // ✅ LISTENERS PARA LAS TARJETAS / BOTONES DEL INICIO

        val btnComunicados = findViewById<ImageView>(R.id.comunicados)
        btnComunicados.setOnClickListener {
            startActivity(Intent(this, Comunicados::class.java))
        }

        val btnCalendario = findViewById<ImageView>(R.id.calendario)
        btnCalendario.setOnClickListener {
            startActivity(Intent(this, Calendario::class.java))
        }

        val btnNotas = findViewById<ImageView>(R.id.notas)
        btnNotas.setOnClickListener {
            startActivity(Intent(this, ItemMateriaNotasActivity::class.java))
        }

        val btnTareas = findViewById<ImageView>(R.id.tareas)
        btnTareas.setOnClickListener {
            startActivity(Intent(this, Tareas::class.java)) // Ajusta al nombre real de tu actividad
        }

        //val btnCarnet = findViewById<ImageView>(R.id.carnet)
        //btnCarnet.setOnClickListener {
            //startActivity(Intent(this, CarnetActivity::class.java)) // Ajusta si se llama distinto
        //}

        val btnUsuario = findViewById<ImageView>(R.id.datospersonales)
        btnUsuario.setOnClickListener {
            startActivity(Intent(this, ProfileActivityEst::class.java))
        }

        // Configurar la barra inferior
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home

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
                    startActivity(Intent(this, ProfileActivityEst::class.java))
                    true
                }
                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}