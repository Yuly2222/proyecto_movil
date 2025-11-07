package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NotasEst : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var rvNotas: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var btnUnirseClase: Button
    private lateinit var inputIdClase: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas_est)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        rvNotas = findViewById(R.id.rvNotas)
        rvNotas.layoutManager = LinearLayoutManager(this)

        inputIdClase = findViewById(R.id.inputIdClase)
        btnUnirseClase = findViewById(R.id.btnUnirseClase)

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_courses
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, InicioEst::class.java)); true }
                R.id.nav_courses -> true
                R.id.nav_calendar -> { startActivity(Intent(this, Calendario::class.java)); true }
                R.id.nav_notifications -> { startActivity(Intent(this, Comunicados::class.java)); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivityEst::class.java)); true }
                else -> false
            }
        }

        btnUnirseClase.setOnClickListener {
            val idClase = inputIdClase.text.toString().trim()
            if (idClase.isEmpty()) {
                Toast.makeText(this, "Ingresa un ID de clase válido", Toast.LENGTH_SHORT).show()
            } else {
                unirseAClase(idClase)
            }
        }

        cargarClasesEstudiante()
    }

    private fun unirseAClase(idClase: String) {
        val user = auth.currentUser ?: return
        val uid = user.uid

        // 🔹 Traer nombre y apellido desde el nodo "usuarios"
        val usuarioRef = database.getReference("usuarios").child(uid)
        usuarioRef.get().addOnSuccessListener { userSnap ->
            val nombre = userSnap.child("nombre").getValue(String::class.java) ?: ""
            val apellido = userSnap.child("apellido").getValue(String::class.java) ?: ""
            val nombreCompleto = "$nombre $apellido".trim()

            val claseRef = database.getReference("clases").child(idClase)
            claseRef.get().addOnSuccessListener { snap ->
                if (!snap.exists()) {
                    Toast.makeText(this, "Clase no encontrada", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Verificar si ya está inscrito
                val estudiantesRef = claseRef.child("estudiantes")
                if (snap.child("estudiantes").hasChild(uid)) {
                    Toast.makeText(this, "Ya estás inscrito en esta clase", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Guardar estudiante en Firebase con nombre completo
                val datosEstudiante = mapOf(
                    "nombre" to nombreCompleto,
                    "notas" to mapOf<String, String>() // Inicialmente vacío
                )

                estudiantesRef.child(uid).setValue(datosEstudiante)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Te has unido a la clase $idClase", Toast.LENGTH_SHORT).show()
                        inputIdClase.text.clear()
                        cargarClasesEstudiante()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al unirse: ${e.message}", Toast.LENGTH_LONG).show()
                    }

            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al buscar clase: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al obtener datos del usuario: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun cargarClasesEstudiante() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        val clasesRef = database.getReference("clases")
        clasesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaNotas = mutableListOf<Nota>()

                for (claseSnap in snapshot.children) {
                    val estudiantesSnap = claseSnap.child("estudiantes")
                    if (estudiantesSnap.hasChild(uid)) {
                        val idClase = claseSnap.child("idClase").getValue(String::class.java) ?: ""
                        val nombre = claseSnap.child("nombre").getValue(String::class.java) ?: ""
                        val descripcion = claseSnap.child("descripcion").getValue(String::class.java) ?: ""

                        val notasSnap = estudiantesSnap.child(uid).child("notas")
                        var promedio = 0.0
                        if (notasSnap.exists()) {
                            var suma = 0.0
                            var count = 0
                            for (nota in notasSnap.children) {
                                val valor = nota.getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                                suma += valor
                                count++
                            }
                            promedio = if (count > 0) suma / count else 0.0
                        }

                        listaNotas.add(
                            Nota(idClase, nombre, descripcion, promedio.toString())
                        )
                    }
                }

                rvNotas.adapter = NotasAdapter(listaNotas)

                if (listaNotas.isEmpty()) {
                    Toast.makeText(this@NotasEst, "No estás inscrito en ninguna clase aún.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@NotasEst, "Error al cargar clases: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
