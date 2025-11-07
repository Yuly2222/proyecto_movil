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

class RegistroAdminActivity : AppCompatActivity() {

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

        // Cambiar color de textos
        txtVolver.setTextColor(Color.WHITE)
        btnSave.setTextColor(Color.WHITE)

        // Opciones para los Spinners
        val tiposDocumento = listOf("CC", "TI", "CE", "Pasaporte")
        val tiposUsuario = listOf("Estudiante", "Profesor", "Admin", "Acudiente")

        // Adaptador genérico para spinners
        fun crearAdaptador(opciones: List<String>): ArrayAdapter<String> {
            return object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, opciones) {
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
            }.apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        spinnerTipoDocumento.adapter = crearAdaptador(tiposDocumento)
        spinnerTipoUsuario.adapter = crearAdaptador(tiposUsuario)

        // Acción del texto "Volver"
        txtVolver.setOnClickListener {
            val intent = Intent(this, InicioAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Acción del botón "Registrarse"
        btnSave.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val apellido = editApellido.text.toString().trim()
            val tipoDocumento = spinnerTipoDocumento.selectedItem?.toString() ?: ""
            val documento = editDocumento.text.toString().trim()
            val telefono = editTelefono.text.toString().trim()
            val direccion = editDireccion.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()
            val tipoUsuario = spinnerTipoUsuario.selectedItem?.toString() ?: ""

            // Validar campos
            if (nombre.isEmpty() || apellido.isEmpty() || documento.isEmpty() ||
                telefono.isEmpty() || direccion.isEmpty() || email.isEmpty() || password.isEmpty()
            ) {
                Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear usuario en Firebase Authentication (solo registro, sin login)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = task.result?.user?.uid
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

                            // Guardar datos del usuario en Realtime Database
                            db.reference.child("usuarios").child(uid).setValue(usuario)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Usuario creado correctamente. Ya puede iniciar sesión.",
                                        Toast.LENGTH_LONG
                                    ).show()
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
            val intent = Intent(this, InicioAdminActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
