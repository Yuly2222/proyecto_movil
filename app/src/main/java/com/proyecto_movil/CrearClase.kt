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
    private var userRol: String = "Desconocido"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_clase)
        supportActionBar?.hide()

        // 🔹 Referencias del layout
        val editNombreClase = findViewById<EditText>(R.id.editNombreClase)
        val editDescripcion = findViewById<EditText>(R.id.editDescripcion)
        val btnGuardarClase = findViewById<Button>(R.id.btnGuardarClase)
        val tvVolver = findViewById<TextView>(R.id.tvVolver)

        // 🔹 Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        clasesRef = database.getReference("clases")
        usuariosRef = database.getReference("usuarios")

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 🔹 Obtener rol del usuario actual
        usuariosRef.child(user.uid).child("tipoUsuario").get().addOnSuccessListener { snapshot ->
            userRol = snapshot.getValue(String::class.java) ?: "Desconocido"

            // 🔹 Configurar botón "Volver" solo para Admin y Profesor
            tvVolver.setOnClickListener {
                val destino = when (userRol) {
                    "Admin" -> InicioAdminActivity::class.java
                    "Profesor" -> InicioProf::class.java
                    else -> null
                }

                if (destino != null) {
                    startActivity(Intent(this, destino))
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                } else {
                    Toast.makeText(this, "Acceso no permitido", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 🔹 Acción: Guardar clase
        btnGuardarClase.setOnClickListener {
            val nombre = editNombreClase.text.toString().trim()
            val descripcion = editDescripcion.text.toString().trim()

            if (nombre.isBlank() || descripcion.isBlank()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = user.uid

            // 🔹 Obtener nombre del profesor desde usuarios/{uid}/nombre
            usuariosRef.child(uid).child("nombre").get().addOnSuccessListener { dataSnapshot ->
                val nombreProfesor = dataSnapshot.getValue(String::class.java) ?: "Desconocido"

                // 🔹 Generar ID único incremental para la clase
                clasesRef.get().addOnSuccessListener { snapshot ->
                    val totalClases = snapshot.childrenCount.toInt() + 1
                    val nuevoId = "CLASE" + totalClases.toString().padStart(3, '0')

                    val clase = mapOf(
                        "idClase" to nuevoId,
                        "nombre" to nombre,
                        "descripcion" to descripcion,
                        "estado" to "Activa",
                        "cantidadEstudiantes" to 0,
                        "profesor" to nombreProfesor,
                        "uidProfesor" to uid
                    )

                    // 🔹 Guardar clase en Firebase
                    clasesRef.child(nuevoId).setValue(clase)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Clase '$nombre' creada correctamente por $nombreProfesor",
                                Toast.LENGTH_LONG
                            ).show()

                            editNombreClase.text.clear()
                            editDescripcion.text.clear()

                            // 🔹 Redirigir según rol
                            val destino = when (userRol) {
                                "Admin" -> InicioAdminActivity::class.java
                                "Profesor" -> InicioProf::class.java
                                else -> null
                            }

                            if (destino != null) {
                                startActivity(Intent(this, destino))
                                finish()
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            }
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
