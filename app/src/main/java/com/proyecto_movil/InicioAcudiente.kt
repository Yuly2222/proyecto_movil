package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class InicioAcudiente : AppCompatActivity() {

    private lateinit var tvNombreHijo: TextView
    private lateinit var tvGradoHijo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_acudiente)
        supportActionBar?.hide()

        tvNombreHijo = findViewById(R.id.tvNombreHijo)
        tvGradoHijo = findViewById(R.id.tvGradoHijo)

        cargarDatosHijo()   // <-- 🔥 CARGAMOS EL HIJO ASIGNADO

        // 🔹 Referencias de las tarjetas
        val btnTareas = findViewById<ImageView>(R.id.tareas)
        val btnNotas = findViewById<ImageView>(R.id.notas)
        val btnCalendario = findViewById<ImageView>(R.id.calendario)
        val btnMensajes = findViewById<ImageView>(R.id.mensajes)

        // 🔹 Acciones con animación
        setAnimatedClick(btnTareas) {
            startActivity(Intent(this, Tareas::class.java))
        }
        setAnimatedClick(btnNotas) {
            startActivity(Intent(this, NotasEst::class.java))
        }
        setAnimatedClick(btnCalendario) {
            startActivity(Intent(this, Calendario::class.java))
        }
        setAnimatedClick(btnMensajes) {
            startActivity(Intent(this, ForoAcudiente::class.java))
        }

        // 🔹 Barra inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_calendar -> { startActivity(Intent(this, Calendario::class.java)); true }
                R.id.nav_notifications -> { startActivity(Intent(this, ForoAcudiente::class.java)); true }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileAcud::class.java)); true }
                else -> false
            }
        }
    }

    // ------------------------------------------------------------
    // 🔥 LÓGICA PARA CARGAR NOMBRE Y GRADO DEL HIJO ASIGNADO
    // ------------------------------------------------------------
    private fun cargarDatosHijo() {
        val auth = FirebaseAuth.getInstance()
        val uidAcudiente = auth.currentUser?.uid ?: return

        val refAcudiente = FirebaseDatabase.getInstance().reference
            .child("usuarios")
            .child(uidAcudiente)

        refAcudiente.get().addOnSuccessListener { snapshot ->

            val uidHijo = snapshot.child("uidEstudianteAsignado").getValue(String::class.java)

            if (uidHijo == null) {
                tvNombreHijo.text = "Sin estudiante asignado"
                tvGradoHijo.text = ""
                return@addOnSuccessListener
            }

            // Ahora traemos los datos del hijo desde usuarios/uidHijo
            val refHijo = FirebaseDatabase.getInstance().reference
                .child("usuarios")
                .child(uidHijo)

            refHijo.get().addOnSuccessListener { snapHijo ->

                val nombre = snapHijo.child("nombre").getValue(String::class.java) ?: "Estudiante"
                val grado = snapHijo.child("grado").getValue(String::class.java) ?: "Sin grado"

                tvNombreHijo.text = nombre
                tvGradoHijo.text = "Grado: $grado"

            }.addOnFailureListener {
                tvNombreHijo.text = "Error cargando estudiante"
                tvGradoHijo.text = ""
            }

        }.addOnFailureListener {
            tvNombreHijo.text = "Error cargando datos"
            tvGradoHijo.text = ""
        }
    }

    /**
     * 💫 Animación fluida con rebote y ligera elevación al hacer clic.
     */
    private fun setAnimatedClick(view: View, action: () -> Unit) {
        view.setOnClickListener {
            view.animate()
                .scaleX(0.93f)
                .scaleY(0.93f)
                .translationZ(8f)
                .setDuration(80)
                .withEndAction {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .translationZ(0f)
                        .setDuration(80)
                        .withEndAction { action() }
                        .start()
                }
                .start()
        }
    }
}
