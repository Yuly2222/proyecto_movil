package com.proyecto_movil

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class VerClasesActivas : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var clasesRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var containerClases: LinearLayout
    private lateinit var userRol: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_clases_activas)

        val tvVolver = findViewById<TextView>(R.id.tvVolver)
        containerClases = findViewById(R.id.containerClases)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        clasesRef = database.getReference("clases")

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_LONG).show()
            return
        }

        val uid = user.uid

        // 🔹 Primero obtenemos el rol del usuario
        val usuarioRef = database.getReference("usuarios").child(uid)
        usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userRol = snapshot.child("rol").getValue(String::class.java) ?: "Desconocido"

                // 🔹 Configurar botón Volver según rol
                tvVolver.setOnClickListener {
                    when (userRol) {
                        "Admin" -> startActivity(Intent(this@VerClasesActivas, InicioAdminActivity::class.java))
                        "Profesor" -> startActivity(Intent(this@VerClasesActivas, InicioProf::class.java))
                        "Estudiante" -> startActivity(Intent(this@VerClasesActivas, InicioEst::class.java))
                        "Acudiente" -> startActivity(Intent(this@VerClasesActivas, InicioAcudiente::class.java))
                        else -> startActivity(Intent(this@VerClasesActivas, VerClasesActivas::class.java))
                    }
                    finish()
                }


                // 🔹 Luego mostramos las clases
                cargarClases(uid)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VerClasesActivas, "Error al obtener rol", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarClases(uid: String) {
        clasesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                containerClases.removeAllViews()
                var tieneClases = false

                for (claseSnap in snapshot.children) {
                    val estado = claseSnap.child("estado").getValue(String::class.java) ?: ""
                    val uidProfesor = claseSnap.child("uidProfesor").getValue(String::class.java) ?: ""

                    // 🔹 Si es Admin → ve todas las clases activas
                    // 🔹 Si es Profesor → ve solo sus clases activas
                    // 🔹 Si es otro → ve todas las clases activas (solo lectura)
                    val puedeVer = when (userRol) {
                        "Admin" -> estado == "Activa"
                        "Profesor" -> estado == "Activa" && uidProfesor == uid
                        else -> estado == "Activa"
                    }

                    if (puedeVer) {
                        tieneClases = true

                        val idClase = claseSnap.child("idClase").getValue(String::class.java) ?: ""
                        val nombre = claseSnap.child("nombre").getValue(String::class.java) ?: ""
                        val descripcion = claseSnap.child("descripcion").getValue(String::class.java) ?: ""
                        val cantidad = claseSnap.child("cantidadEstudiantes").getValue(Int::class.java) ?: 0
                        val profesor = claseSnap.child("profesor").getValue(String::class.java) ?: ""

                        // 🔹 Crear tarjeta visual
                        val tarjeta = LinearLayout(this@VerClasesActivas).apply {
                            orientation = LinearLayout.VERTICAL
                            setPadding(24, 24, 24, 24)
                            setBackgroundResource(R.drawable.bg_card)
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 0, 0, 24)
                            }
                        }

                        val tvNombre = TextView(this@VerClasesActivas).apply {
                            text = "Nombre: $nombre"
                            textSize = 18f
                            setTypeface(null, Typeface.BOLD)
                            setTextColor(resources.getColor(R.color.textSecondary, null))
                        }

                        val tvDescripcion = TextView(this@VerClasesActivas).apply {
                            text = "Descripción: $descripcion"
                            textSize = 16f
                            setPadding(0, 4, 0, 0)
                            setTextColor(resources.getColor(R.color.black, null))
                        }

                        val tvEstado = TextView(this@VerClasesActivas).apply {
                            text = "Estado: $estado"
                            textSize = 14f
                            setPadding(0, 4, 0, 0)
                            setTextColor(resources.getColor(R.color.teal_700, null))
                        }

                        val tvCantidad = TextView(this@VerClasesActivas).apply {
                            text = "Estudiantes: $cantidad"
                            textSize = 14f
                            setPadding(0, 4, 0, 0)
                            setTextColor(resources.getColor(R.color.black, null))
                        }

                        val tvProfesor = TextView(this@VerClasesActivas).apply {
                            text = "Profesor: $profesor"
                            textSize = 14f
                            setPadding(0, 4, 0, 0)
                            setTextColor(resources.getColor(R.color.black, null))
                        }

                        tarjeta.addView(tvNombre)
                        tarjeta.addView(tvDescripcion)
                        tarjeta.addView(tvEstado)
                        tarjeta.addView(tvCantidad)
                        tarjeta.addView(tvProfesor)

                        // 🔹 Si el usuario es Admin → botón Editar
                        if (userRol == "Admin") {
                            val btnEditar = Button(this@VerClasesActivas).apply {
                                text = "Editar"
                                textSize = 14f
                                setPadding(8, 4, 8, 4)
                                setBackgroundColor(resources.getColor(R.color.teal_700, null))
                                setTextColor(resources.getColor(android.R.color.white, null))
                                setOnClickListener {
                                    mostrarDialogoEditar(idClase, nombre, descripcion, cantidad)
                                }
                            }
                            tarjeta.addView(btnEditar)
                        }

                        containerClases.addView(tarjeta)
                    }
                }

                if (!tieneClases) {
                    val tvEmpty = TextView(this@VerClasesActivas).apply {
                        text = "No hay clases activas."
                        gravity = Gravity.CENTER
                        textSize = 16f
                        setTextColor(resources.getColor(R.color.white, null))
                        setPadding(0, 50, 0, 0)
                    }
                    containerClases.addView(tvEmpty)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VerClasesActivas, "Error al cargar clases", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun mostrarDialogoEditar(idClase: String, nombre: String, descripcion: String, cantidad: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar clase $idClase")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(32, 16, 32, 16)

        val inputNombre = EditText(this).apply {
            hint = "Nombre"
            setText(nombre)
        }

        val inputDescripcion = EditText(this).apply {
            hint = "Descripción"
            setText(descripcion)
            inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }

        val inputCantidad = EditText(this).apply {
            hint = "Cantidad de estudiantes"
            setText(cantidad.toString())
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(inputNombre)
        layout.addView(inputDescripcion)
        layout.addView(inputCantidad)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevosDatos = mapOf(
                "nombre" to inputNombre.text.toString(),
                "descripcion" to inputDescripcion.text.toString(),
                "cantidadEstudiantes" to (inputCantidad.text.toString().toIntOrNull() ?: 0)
            )

            clasesRef.child(idClase).updateChildren(nuevosDatos)
                .addOnSuccessListener {
                    Toast.makeText(this, "Clase actualizada correctamente", Toast.LENGTH_SHORT).show()
                    recreate()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
}
