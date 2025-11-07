package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

data class ForoGrupo(
    val idForo: String? = null,
    val titulo: String? = null,
    val descripcion: String? = null,
    val grado: String? = null,
    val jefeGrado: String? = null,
    val uidJefe: String? = null,
    val numAcudientes: Int = 0,
    val creadoPor: String? = null
)

class CrearForoAdmin : AppCompatActivity() {

    private lateinit var editTitulo: EditText
    private lateinit var editDescripcion: EditText
    private lateinit var spinnerGrado: Spinner
    private lateinit var tvJefeGrado: TextView
    private lateinit var tvNumeroAcudientes: TextView
    private lateinit var btnCrear: Button
    private lateinit var tvVolver: TextView

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val grados = listOf("1°","2°","3°","4°","5°","6°","7°","8°","9°","10°","11°")

    private var jefeNombre: String? = null
    private var jefeUid: String? = null
    private var numAcudientes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_foro_admin)
        supportActionBar?.hide()

        editTitulo = findViewById(R.id.editTituloForo)
        editDescripcion = findViewById(R.id.editDescripcionForo)
        spinnerGrado = findViewById(R.id.spinnerGradoForo)
        tvJefeGrado = findViewById(R.id.tvJefeGrado)
        tvNumeroAcudientes = findViewById(R.id.tvNumeroAcudientes)
        btnCrear = findViewById(R.id.btnCrearForo)
        tvVolver = findViewById(R.id.tvVolver)

        spinnerGrado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, grados)

        spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val grado = grados[pos]
                buscarJefeGrado(grado)
                contarAcudientesPorGrado(grado)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnCrear.setOnClickListener {
            val titulo = editTitulo.text.toString().trim()
            val descripcion = editDescripcion.text.toString().trim()
            val grado = spinnerGrado.selectedItem.toString()

            if (titulo.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idForo = db.reference.child("foros").push().key ?: return@setOnClickListener
            val foro = ForoGrupo(
                idForo = idForo,
                titulo = titulo,
                descripcion = descripcion,
                grado = grado,
                jefeGrado = jefeNombre,
                uidJefe = jefeUid,
                numAcudientes = numAcudientes,
                creadoPor = auth.currentUser?.uid
            )

            db.reference.child("foros").child(idForo).setValue(foro)
                .addOnSuccessListener {
                    Toast.makeText(this, "Foro creado correctamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, InicioAdminActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al crear foro: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        tvVolver.setOnClickListener {
            startActivity(Intent(this, InicioAdminActivity::class.java))
            finish()
        }
    }

    // 🔹 Buscar profesor jefe del grado
    private fun buscarJefeGrado(grado: String) {
        db.reference.child("usuarios")
            .orderByChild("tipoUsuario")
            .equalTo("Profesor")
            .get()
            .addOnSuccessListener { snapshot ->
                jefeNombre = null
                jefeUid = null
                for (profSnap in snapshot.children) {
                    val dirCurso = profSnap.child("directorCurso").getValue(String::class.java)
                    if (dirCurso == grado) {
                        val nombre = profSnap.child("nombre").getValue(String::class.java) ?: ""
                        val apellido = profSnap.child("apellido").getValue(String::class.java) ?: ""
                        jefeNombre = "$nombre $apellido"
                        jefeUid = profSnap.key
                        break
                    }
                }
                tvJefeGrado.text = jefeNombre ?: "No asignado"
            }
            .addOnFailureListener {
                tvJefeGrado.text = "Error al cargar"
            }
    }

    // 🔹 Contar acudientes que tienen estudiantes en ese grado
    private fun contarAcudientesPorGrado(grado: String) {
        db.reference.child("usuarios")
            .orderByChild("tipoUsuario")
            .equalTo("Estudiante")
            .get()
            .addOnSuccessListener { snapshotEst ->
                val estudiantesUid = mutableListOf<String>()
                for (estSnap in snapshotEst.children) {
                    val gradoEst = estSnap.child("grado").getValue(String::class.java)
                    if (gradoEst == grado) {
                        estudiantesUid.add(estSnap.key ?: "")
                    }
                }

                if (estudiantesUid.isEmpty()) {
                    tvNumeroAcudientes.text = "0"
                    numAcudientes = 0
                    return@addOnSuccessListener
                }

                // Buscar acudientes cuyo uidEstudianteAsignado esté en la lista
                db.reference.child("usuarios")
                    .orderByChild("tipoUsuario")
                    .equalTo("Acudiente")
                    .get()
                    .addOnSuccessListener { snapshotAcu ->
                        var contador = 0
                        for (acuSnap in snapshotAcu.children) {
                            val uidEstAsig = acuSnap.child("uidEstudianteAsignado").getValue(String::class.java)
                            if (uidEstAsig != null && estudiantesUid.contains(uidEstAsig)) {
                                contador++
                            }
                        }
                        numAcudientes = contador
                        tvNumeroAcudientes.text = contador.toString()
                    }
                    .addOnFailureListener {
                        tvNumeroAcudientes.text = "Error"
                    }
            }
            .addOnFailureListener {
                tvNumeroAcudientes.text = "Error"
            }
    }
}
