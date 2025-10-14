package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.LinearLayout
import androidx.cardview.widget.CardView

class InicioProf : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicioprof)

        val btnComunicados = findViewById<ImageView>(R.id.comunicados)
        btnComunicados.setOnClickListener {
            startActivity(Intent(this, Comunicados::class.java))
        }

        val btnCalendario = findViewById<ImageView>(R.id.calendario)
        btnCalendario.setOnClickListener {
            startActivity(Intent(this, Calendario::class.java))
        }

        // FIX: Change AdapterNotas to the correct Activity, e.g., NotasActivity
        val btnNotas = findViewById<ImageView>(R.id.notas)
        btnNotas.setOnClickListener {
            // Replace AdapterNotas::class.java with your new activity
            startActivity(Intent(this, NotasEst::class.java))
        }

        val btnUsuario = findViewById<ImageView>(R.id.datospersonales)
        btnUsuario.setOnClickListener {
            startActivity(Intent(this, ProfileActivityEst::class.java))
        }

        // 🔹 Oculta la ActionBar (si aún aparece)
        supportActionBar?.hide()

        // 🔹 Configuración de márgenes de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Referencia al LinearLayout
        val clasesLayout = findViewById<CardView>(R.id.clasesLayout)

        // 🔹 Evento clic para abrir la actividad Clases
        clasesLayout.setOnClickListener {
            val intent = Intent(this, Clases::class.java)
            startActivity(intent)
        }
    }
}
