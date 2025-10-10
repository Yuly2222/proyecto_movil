package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class Clases : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clases)

        val btnCrearClase = findViewById<Button>(R.id.btnCrearClase)
        val btnVerActivas = findViewById<Button>(R.id.btnVerActivas)
        val tvVolver = findViewById<TextView>(R.id.tvVolver)


        tvVolver.setOnClickListener {
            val intent = Intent(this, InicioProf::class.java)
            startActivity(intent)
            finish()
        }

        btnCrearClase.setOnClickListener {
            val intent = Intent(this, CrearClase::class.java)
            startActivity(intent)
        }

        btnVerActivas.setOnClickListener {
            val intent = Intent(this, VerClasesActivas::class.java)
            startActivity(intent)        }
    }
}