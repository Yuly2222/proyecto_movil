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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_clase)

        val editNombreClase = findViewById<EditText>(R.id.editNombreClase)
        val editDescripcion = findViewById<EditText>(R.id.editDescripcion)
        val btnGuardarClase = findViewById<Button>(R.id.btnGuardarClase)

        val tvVolver = findViewById<TextView>(R.id.tvVolver)

        tvVolver.setOnClickListener {
            val intent = Intent(this, Clases::class.java) // ← Cambiado a Clases
            startActivity(intent)
            finish()
        }

        // 🔹 Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        clasesRef = database.getReference("clases")
        usuariosRef = database.getReference("usuarios")

        btnGuardarClase.setOnClickListener {
            val nombre = editNombreClase.text.toString().trim()
            val descripcion = editDescripcion.text.toString().trim()

            if (nombre.isBlank() || descripcion.isBlank()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            if (user == null) {
                Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val uid = user.uid

            // 🔹 Obtener el nombre del profesor desde "usuarios/{uid}/nombre"
            usuariosRef.child(uid).child("nombre").get().addOnSuccessListener { dataSnapshot ->
                val nombreProfesor = dataSnapshot.getValue(String::class.java) ?: "Desconocido"

                // 🔹 Obtener total de clases para generar un nuevo ID único
                clasesRef.get().addOnSuccessListener { snapshot ->
                    val totalClases = snapshot.childrenCount.toInt() + 1
                    val nuevoId = "CLASE" + totalClases.toString().padStart(3, '0')

                    val clase = mapOf(
                        "idClase" to nuevoId,
                        "nombre" to nombre,
                        "descripcion" to descripcion,
                        "estado" to "Activa",              // Por defecto
                        "cantidadEstudiantes" to 0,        // Por defecto
                        "profesor" to nombreProfesor,      // 🔹 Nombre del usuario logueado
                        "uidProfesor" to uid               // Guardar también el UID
                    )

                    // 🔹 Guardar la clase bajo el ID generado
                    clasesRef.child(nuevoId).setValue(clase)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Clase '$nombre' creada por $nombreProfesor",
                                Toast.LENGTH_LONG
                            ).show()

                            // Limpiar campos
                            editNombreClase.text.clear()
                            editDescripcion.text.clear()

                            // 🔹 Navegar a InicioProf
                            val intent = Intent(this, InicioProf::class.java)
                            startActivity(intent)
                            finish() // Cierra la actividad actual
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error al generar ID: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al obtener nombre del profesor", Toast.LENGTH_LONG).show()
            }
        }
    }
}
