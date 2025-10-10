package com.proyecto_movil

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registro : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        // Ocultar la barra superior
        supportActionBar?.hide()

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        // Referencias a vistas
        val txtVolver = findViewById<TextView>(R.id.txtVolver)
        val editNombre = findViewById<EditText>(R.id.editNombre)
        val editApellido = findViewById<EditText>(R.id.editApellido)
        val spinnerTipoDocumento = findViewById<Spinner>(R.id.spinnerTipoDocumento)
        val editDocumento = findViewById<EditText>(R.id.editDocumento)
        val editTelefono = findViewById<EditText>(R.id.editTelefono)
        val editDireccion = findViewById<EditText>(R.id.editDireccion)
        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val spinnerTipoUsuario = findViewById<Spinner>(R.id.spinnerTipoUsuario)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Cambiar color de textos directamente
        txtVolver.setTextColor(Color.WHITE)
        btnSave.setTextColor(Color.WHITE)

        // Configurar opciones de los Spinners
        val tiposDocumento = listOf("CC", "TI", "CE", "Pasaporte")
        val tiposUsuario = listOf("Estudiante", "Profesor")

        // Adaptador con color personalizado para Tipo de Documento
        val adapterDocumento = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            tiposDocumento
        ) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(Color.WHITE) // Color del texto seleccionado
                view.textSize = 16f
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(Color.BLACK) // Color del texto en el menú desplegable
                view.textSize = 16f
                return view
            }
        }
        adapterDocumento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoDocumento.adapter = adapterDocumento

        // Adaptador con color personalizado para Tipo de Usuario
        val adapterUsuario = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            tiposUsuario
        ) {
            override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(Color.WHITE)
                view.textSize = 16f
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(Color.BLACK)
                view.textSize = 16f
                return view
            }
        }
        adapterUsuario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoUsuario.adapter = adapterUsuario

        // Acción del texto "Volver"
        txtVolver.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        // Acción del botón "Registrarse"
        btnSave.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val apellido = editApellido.text.toString().trim()
            val tipoDocumento = spinnerTipoDocumento.selectedItem.toString()
            val documento = editDocumento.text.toString().trim()
            val telefono = editTelefono.text.toString().trim()
            val direccion = editDireccion.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()
            val tipoUsuario = spinnerTipoUsuario.selectedItem.toString()

            // Validar campos
            if (nombre.isEmpty() || apellido.isEmpty() || documento.isEmpty() ||
                telefono.isEmpty() || direccion.isEmpty() ||
                email.isEmpty() || password.isEmpty()
            ) {
                Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear usuario en Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        if (uid != null) {
                            val usuario = mapOf(
                                "nombre" to nombre,
                                "apellido" to apellido,
                                "tipoDocumento" to tipoDocumento,
                                "documento" to documento,
                                "telefono" to telefono,
                                "direccion" to direccion,
                                "email" to email,
                                "tipoUsuario" to tipoUsuario
                            )

                            // Guardar en Realtime Database
                            db.reference.child("usuarios").child(uid).setValue(usuario)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Registro exitoso, bienvenido $nombre",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intent = if (tipoUsuario == "Estudiante") {
                                        Intent(this, InicioEst::class.java)
                                    } else {
                                        Intent(this, InicioProf::class.java)
                                    }
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Error al guardar datos: ${it.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Error al registrar: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}
