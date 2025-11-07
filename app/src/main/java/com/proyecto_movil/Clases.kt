package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Clases : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usuariosRef: DatabaseReference
    private var userRol: String = "Desconocido"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clases)

        val btnCrearClase = findViewById<Button>(R.id.btnCrearClase)
        val btnVerActivas = findViewById<Button>(R.id.btnVerActivas)
        val tvVolver = findViewById<TextView>(R.id.tvVolver)

        // 🔹 Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usuariosRef = database.getReference("usuarios")

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_LONG).show()
            return
        }

        // 🔹 Deshabilitamos el botón hasta obtener el rol
        tvVolver.isEnabled = false

        // 🔹 Obtener rol del usuario
        usuariosRef.child(user.uid).child("tipoUsuario").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userRol = task.result?.getValue(String::class.java) ?: "Desconocido"

                // 🔹 Habilitamos el botón y asignamos acción según rol
                tvVolver.isEnabled = true
                tvVolver.setOnClickListener {
                    val destino = when (userRol) {
                        "Admin" -> InicioAdminActivity::class.java
                        "Profesor" -> InicioProf::class.java
                        "Estudiante" -> InicioEst::class.java
                        "Acudiente" -> InicioAcudiente::class.java
                        else -> MainActivity::class.java
                    }
                    startActivity(Intent(this, destino))
                    finish()
                }

            } else {
                // En caso de error, habilitar el botón y enviar a MainActivity
                tvVolver.isEnabled = true
                tvVolver.setOnClickListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }

        // 🔹 Botón: Crear nueva clase
        btnCrearClase.setOnClickListener {
            startActivity(Intent(this, CrearClase::class.java))
        }

        // 🔹 Botón: Ver clases activas
        btnVerActivas.setOnClickListener {
            startActivity(Intent(this, VerClasesActivas::class.java))
        }
    }
}
