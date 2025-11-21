package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CrearClase : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var clasesRef: DatabaseReference
    private lateinit var usuariosRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var spinnerProfesores: Spinner

    private var userRol: String = "Desconocido"

    private val listaProfesores = mutableListOf<String>()
    private val listaProfesoresUID = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_clase)
        supportActionBar?.hide()

        val editNombreClase = findViewById<EditText>(R.id.editNombreClase)
        val editDescripcion = findViewById<EditText>(R.id.editDescripcion)
        val btnGuardarClase = findViewById<Button>(R.id.btnGuardarClase)
        val tvVolver = findViewById<TextView>(R.id.tvVolver)
        spinnerProfesores = findViewById(R.id.spinnerProfesores)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        clasesRef = database.getReference("clases")
        usuariosRef = database.getReference("usuarios")

        val user = auth.currentUser ?: return

        usuariosRef.child(user.uid).child("tipoUsuario").get().addOnSuccessListener { snap ->

            userRol = snap.getValue(String::class.java) ?: "Desconocido"

            if (userRol != "Admin") {
                Toast.makeText(this, "Solo los administradores pueden crear clases.", Toast.LENGTH_LONG).show()
                finish()
                return@addOnSuccessListener
            }

            cargarProfesores()
        }

        tvVolver.setOnClickListener {
            startActivity(Intent(this, InicioAdminActivity::class.java))
            finish()
        }

        btnGuardarClase.setOnClickListener {

            val nombre = editNombreClase.text.toString().trim()
            val descripcion = editDescripcion.text.toString().trim()

            if (nombre.isBlank() || descripcion.isBlank()) {
                Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val index = spinnerProfesores.selectedItemPosition
            if (index == 0) {     // 0 = "Seleccione un profesor"
                Toast.makeText(this, "Debes seleccionar un profesor.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uidProfesor = listaProfesoresUID[index]
            val nombreProfesor = listaProfesores[index]

            // 🔥 Crear ID incremental
            clasesRef.get().addOnSuccessListener { snapshot ->
                val totalClases = snapshot.childrenCount.toInt() + 1
                val idClase = "CLASE" + totalClases.toString().padStart(3, '0')

                val clase = mapOf(
                    "idClase" to idClase,
                    "nombre" to nombre,
                    "descripcion" to descripcion,
                    "estado" to "Activa",
                    "cantidadEstudiantes" to 0,
                    "profesor" to nombreProfesor,
                    "uidProfesor" to uidProfesor
                )

                clasesRef.child(idClase).setValue(clase)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Clase '$nombre' creada y asignada a $nombreProfesor",
                            Toast.LENGTH_LONG
                        ).show()

                        editNombreClase.text.clear()
                        editDescripcion.text.clear()

                        startActivity(Intent(this, InicioAdminActivity::class.java))
                        finish()
                    }
            }
        }
    }

    // 🟩 CARGAR LISTA DE PROFESORES
    private fun cargarProfesores() {

        usuariosRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listaProfesores.clear()
                listaProfesoresUID.clear()

                // 🔥 PRIMER ITEM — TEXTO POR DEFECTO
                listaProfesores.add("Seleccione un profesor")
                listaProfesoresUID.add("NONE")

                for (userSnap in snapshot.children) {
                    val tipo = userSnap.child("tipoUsuario").getValue(String::class.java)
                    if (tipo == "Profesor") {

                        val nombre = userSnap.child("nombre").getValue(String::class.java) ?: "Sin nombre"
                        val apellido = userSnap.child("apellido").getValue(String::class.java) ?: ""
                        val uid = userSnap.key ?: continue

                        listaProfesores.add("$nombre $apellido")
                        listaProfesoresUID.add(uid)
                    }
                }

                val adapter = ArrayAdapter(this@CrearClase, android.R.layout.simple_spinner_item, listaProfesores)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerProfesores.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CrearClase, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
