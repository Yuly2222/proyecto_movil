package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

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
        val txtVolver = findViewById<TextView>(R.id.txtVolver)

        // Arrays con opción inicial
        val tiposDoc = arrayOf("Seleccione tipo de documento", "CC", "TI", "CE", "Pasaporte")
        val tiposUsuario = arrayOf("Seleccione tipo de usuario", "Estudiante", "Profesor", "Acudiente", "Admin")

        configurarSpinner(spinnerTipoDocumento, tiposDoc)
        configurarSpinner(spinnerTipoUsuario, tiposUsuario)

        txtVolver.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        btnSave.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val apellido = editApellido.text.toString().trim()
            val tipoDoc = spinnerTipoDocumento.selectedItem.toString()
            val documento = editDocumento.text.toString().trim()
            val telefono = editTelefono.text.toString().trim()
            val direccion = editDireccion.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()
            val tipoUsuario = spinnerTipoUsuario.selectedItem.toString()

            if (nombre.isEmpty() || apellido.isEmpty() || documento.isEmpty() ||
                telefono.isEmpty() || direccion.isEmpty() || email.isEmpty() || password.isEmpty()
            ) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tipoDoc == "Seleccione tipo de documento") {
                Toast.makeText(this, "Debes seleccionar un tipo de documento", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tipoUsuario == "Seleccione tipo de usuario") {
                Toast.makeText(this, "Debes seleccionar un tipo de usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear usuario en FirebaseAuth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid

                        val usuario = mapOf(
                            "nombre" to nombre,
                            "apellido" to apellido,
                            "tipoDocumento" to tipoDoc,
                            "documento" to documento,
                            "telefono" to telefono,
                            "direccion" to direccion,
                            "email" to email,
                            "tipoUsuario" to tipoUsuario
                        )

                        if (uid != null) {
                            db.reference.child("usuarios").child(uid)
                                .setValue(usuario)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                                    // Limpiar campos
                                    editNombre.text.clear()
                                    editApellido.text.clear()
                                    editDocumento.text.clear()
                                    editTelefono.text.clear()
                                    editDireccion.text.clear()
                                    editEmail.text.clear()
                                    editPassword.text.clear()
                                    spinnerTipoDocumento.setSelection(0)
                                    spinnerTipoUsuario.setSelection(0)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    // Método reutilizable para configurar cualquier Spinner
    private fun configurarSpinner(spinner: Spinner, items: Array<String>) {
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            items
        ) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(getColor(android.R.color.white)) // Texto blanco cuando está seleccionado
                return view
            }

            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(getColor(android.R.color.black)) // Texto negro en el menú desplegable
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}
