package com.proyecto_movil

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class Asistencia: AppCompatActivity() {

    private lateinit var rvEstudiantes: RecyclerView
    private lateinit var btnConfirmar: MaterialButton
    private lateinit var estudiantes: MutableList<Estudiante>
    private lateinit var idClase: String
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asistencia)

        rvEstudiantes = findViewById(R.id.rvEstudiantes)
        btnConfirmar = findViewById(R.id.btnConfirmarAsistencia)
        database = FirebaseDatabase.getInstance()

        idClase = intent.getStringExtra("idClase") ?: return

        // 🔹 Cargar estudiantes de la clase
        cargarEstudiantes()

        btnConfirmar.setOnClickListener {
            confirmarAsistencia()
        }
    }

    private fun cargarEstudiantes() {
        val ref = database.getReference("clases").child(idClase).child("estudiantes")
        ref.get().addOnSuccessListener { snapshot ->
            estudiantes = mutableListOf()
            for (child in snapshot.children) {
                val nombre = child.child("nombre").getValue(String::class.java) ?: "Sin Nombre"
                val uid = child.key ?: ""
                estudiantes.add(Estudiante(uid, nombre))
            }

            rvEstudiantes.layoutManager = LinearLayoutManager(this)
            rvEstudiantes.adapter = AsistenciaAdapter(estudiantes)
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar estudiantes", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmarAsistencia() {
        val fechaHora = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val asistenciasRef = database.getReference("clases").child(idClase)
            .child("asistencias").child(fechaHora.replace(" ", "_"))

        val listaPresentes = estudiantes.filter { it.presente }.associate { it.uid to true }

        asistenciasRef.setValue(listaPresentes).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Asistencia registrada", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al registrar asistencia", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
