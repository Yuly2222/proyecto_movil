package com.proyecto_movil

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class VerClasesActivas : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var clasesRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var containerClases: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_clases_activas)

        val tvVolver = findViewById<TextView>(R.id.tvVolver)

        tvVolver.setOnClickListener {
            val intent = Intent(this, Clases::class.java)
            startActivity(intent)
            finish()
        }

        containerClases = findViewById(R.id.containerClases)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        clasesRef = database.getReference("clases")

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_LONG).show()
            return
        }
        val uidProfesor = user.uid

        // Leer clases desde Firebase
        clasesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                containerClases.removeAllViews() // Limpiar antes de agregar

                for (claseSnap in snapshot.children) {
                    val estado = claseSnap.child("estado").getValue(String::class.java) ?: ""
                    val uidClaseProfesor = claseSnap.child("uidProfesor").getValue(String::class.java) ?: ""

                    // Filtrar solo las clases activas de este profesor
                    if (estado == "Activa" && uidClaseProfesor == uidProfesor) {
                        val nombre = claseSnap.child("nombre").getValue(String::class.java) ?: ""
                        val descripcion = claseSnap.child("descripcion").getValue(String::class.java) ?: ""
                        val cantidadEstudiantes = claseSnap.child("cantidadEstudiantes").getValue(Int::class.java) ?: 0
                        val idClase = claseSnap.child("idClase").getValue(String::class.java) ?: ""

                        // Crear la tarjeta dinámicamente
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

                        // Nombre de la clase
                        val tvNombre = TextView(this@VerClasesActivas).apply {
                            text = nombre
                            setTextColor(resources.getColor(R.color.textSecondary, null))
                            textSize = 18f
                            setTypeface(null, Typeface.BOLD)
                        }

                        // Descripción
                        val tvDescripcion = TextView(this@VerClasesActivas).apply {
                            text = descripcion
                            setTextColor(resources.getColor(R.color.black, null))
                            textSize = 16f
                            setPadding(0, 4, 0, 0)
                        }

                        // Estado
                        val tvEstado = TextView(this@VerClasesActivas).apply {
                            text = "Estado: $estado"
                            setTextColor(resources.getColor(R.color.teal_700, null))
                            textSize = 14f
                            setPadding(0, 4, 0, 0)
                        }

                        // Cantidad de estudiantes
                        val tvCantidad = TextView(this@VerClasesActivas).apply {
                            text = "Estudiantes: $cantidadEstudiantes"
                            setTextColor(resources.getColor(R.color.black, null))
                            textSize = 14f
                            setPadding(0, 4, 0, 0)
                        }

                        // Agregar todo a la tarjeta
                        tarjeta.addView(tvNombre)
                        tarjeta.addView(tvDescripcion)
                        tarjeta.addView(tvEstado)
                        tarjeta.addView(tvCantidad)

                        // Agregar la tarjeta al contenedor
                        containerClases.addView(tarjeta)
                    }
                }

                if (containerClases.childCount == 0) {
                    val tvEmpty = TextView(this@VerClasesActivas).apply {
                        text = "No tienes clases activas."
                        setTextColor(resources.getColor(R.color.black, null))
                        textSize = 16f
                        gravity = Gravity.CENTER
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 50, 0, 0)
                        }
                    }
                    containerClases.addView(tvEmpty)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VerClasesActivas, "Error al cargar clases", Toast.LENGTH_LONG).show()
            }
        })
    }
}
