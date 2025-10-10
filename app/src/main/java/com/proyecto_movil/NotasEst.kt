package com.proyecto_movil

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotasEst : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas_est)

        val recyclerView = findViewById<RecyclerView>(R.id.rvNotas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val listaNotas = listOf(
            Nota("01", "Lorem Ipsum", "2h. Semana", "3.5"),
            Nota("02", "Lorem Ipsum", "2h. Semana", "4.0"),
        )
        recyclerView.adapter = NotasAdapter(listaNotas)
    }
}