package com.proyecto_movil

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroAdminActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        // Base
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

        // Estudiante
        val layoutGrado = findViewById<LinearLayout>(R.id.layoutGrado)
        val spinnerGrado = findViewById<Spinner>(R.id.spinnerGrado)

        // Profesor
        val layoutProfesor = findViewById<LinearLayout>(R.id.layoutProfesor)
        val spinnerDirectorCurso = findViewById<Spinner>(R.id.spinnerDirectorCurso)
        val spinnerAsignatura = findViewById<Spinner>(R.id.spinnerAsignatura)

        // Acudiente
        val layoutEstudiante = findViewById<LinearLayout>(R.id.layoutEstudiante)
        val spinnerEstudiantes = findViewById<Spinner>(R.id.spinnerEstudiantes)

        val btnSave = findViewById<Button>(R.id.btnSave)

        txtVolver.setTextColor(Color.WHITE)
        btnSave.setTextColor(Color.WHITE)

        // Datos para spinners
        val tiposDocumento = listOf("CC", "TI", "CE", "Pasaporte")
        val tiposUsuario = listOf("Estudiante", "Profesor", "Admin", "Acudiente")
        val grados = listOf("1°","2°","3°","4°","5°","6°","7°","8°","9°","10°","11°")
        val opcionesDirectorCurso = listOf("No") + grados
        val asignaturas = listOf(
            "Matemáticas","Lengua Castellana","Inglés","Ciencias Naturales",
            "Ciencias Sociales","Educación Física","Arte","Música",
            "Tecnología","Informática","Ética","Religión"
        )

        fun adaptador(opciones: List<String>) =
            object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, opciones) {
                override fun getView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                    val v = super.getView(position, convertView, parent) as TextView
                    v.setTextColor(Color.WHITE); v.textSize = 16f
                    return v
                }
                override fun getDropDownView(position: Int, convertView: View?, parent: android.view.ViewGroup): View {
                    val v = super.getDropDownView(position, convertView, parent) as TextView
                    v.setTextColor(Color.BLACK)
                    return v
                }
            }.apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerTipoDocumento.adapter = adaptador(tiposDocumento)
        spinnerTipoUsuario.adapter = adaptador(tiposUsuario)
        spinnerGrado.adapter = adaptador(grados)
        spinnerDirectorCurso.adapter = adaptador(opcionesDirectorCurso)
        spinnerAsignatura.adapter = adaptador(asignaturas)

        // Cargar estudiantes (acudiente)
        val estudiantesList = mutableListOf<String>()
        val estudiantesUidMap = mutableMapOf<String, String>()

        fun cargarEstudiantes() {
            db.reference.child("usuarios")
                .orderByChild("tipoUsuario")
                .equalTo("Estudiante")
                .get()
                .addOnSuccessListener { snap ->
                    estudiantesList.clear(); estudiantesUidMap.clear()
                    if (snap.exists()) {
                        for (u in snap.children) {
                            val nombre = u.child("nombre").getValue(String::class.java) ?: continue
                            val apellido = u.child("apellido").getValue(String::class.java) ?: ""
                            val full = "$nombre $apellido".trim()
                            val uid = u.key ?: continue
                            estudiantesList.add(full)
                            estudiantesUidMap[full] = uid
                        }
                        spinnerEstudiantes.adapter =
                            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estudiantesList)
                    } else {
                        Toast.makeText(this, "No se encontraron estudiantes", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al cargar estudiantes: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Mostrar/ocultar secciones según tipoUsuario
        spinnerTipoUsuario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when (tiposUsuario[pos]) {
                    "Estudiante" -> {
                        layoutGrado.visibility = View.VISIBLE
                        layoutProfesor.visibility = View.GONE
                        layoutEstudiante.visibility = View.GONE
                    }
                    "Profesor" -> {
                        layoutGrado.visibility = View.GONE
                        layoutProfesor.visibility = View.VISIBLE
                        layoutEstudiante.visibility = View.GONE
                    }
                    "Acudiente" -> {
                        layoutGrado.visibility = View.GONE
                        layoutProfesor.visibility = View.GONE
                        layoutEstudiante.visibility = View.VISIBLE
                        cargarEstudiantes()
                    }
                    else -> {
                        layoutGrado.visibility = View.GONE
                        layoutProfesor.visibility = View.GONE
                        layoutEstudiante.visibility = View.GONE
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Volver
        txtVolver.setOnClickListener {
            startActivity(Intent(this, InicioAdminActivity::class.java))
            finish()
        }

        // Guardar
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

            if (nombre.isEmpty() || apellido.isEmpty() || documento.isEmpty() ||
                telefono.isEmpty() || direccion.isEmpty() || email.isEmpty() || password.isEmpty()
            ) {
                Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validaciones específicas
            if (tipoUsuario == "Acudiente" &&
                (spinnerEstudiantes.adapter == null || spinnerEstudiantes.selectedItem == null)
            ) {
                Toast.makeText(this, "Seleccione un estudiante", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (tipoUsuario == "Profesor" &&
                (spinnerAsignatura.adapter == null || spinnerAsignatura.selectedItem == null)
            ) {
                Toast.makeText(this, "Seleccione la asignatura", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = task.result?.user?.uid ?: return@addOnCompleteListener
                        val data = mutableMapOf(
                            "nombre" to nombre,
                            "apellido" to apellido,
                            "tipoDocumento" to tipoDocumento,
                            "documento" to documento,
                            "telefono" to telefono,
                            "direccion" to direccion,
                            "email" to email,
                            "tipoUsuario" to tipoUsuario
                        )

                        when (tipoUsuario) {
                            "Estudiante" -> {
                                data["grado"] = spinnerGrado.selectedItem.toString()
                            }
                            "Profesor" -> {
                                data["directorCurso"] = spinnerDirectorCurso.selectedItem.toString() // "No" o "X°"
                                data["asignatura"] = spinnerAsignatura.selectedItem.toString()
                            }
                            "Acudiente" -> {
                                val estSel = spinnerEstudiantes.selectedItem?.toString() ?: ""
                                val uidEst = estudiantesUidMap[estSel] ?: ""
                                data["estudianteAsignado"] = estSel
                                data["uidEstudianteAsignado"] = uidEst
                            }
                        }

                        db.reference.child("usuarios").child(uid).setValue(data)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, InicioAdminActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}


