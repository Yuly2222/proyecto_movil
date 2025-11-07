package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ClasesEstudiante : AppCompatActivity() {

    private lateinit var rvClases: RecyclerView
    private lateinit var clasesAdapter: ClasesAdapter
    private lateinit var clasesRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val listaClases = mutableListOf<Clase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clases_estudiante)
        supportActionBar?.hide()

        val tvVolver = findViewById<TextView>(R.id.tvVolver)
        rvClases = findViewById(R.id.rvClasesEst)
        rvClases.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        clasesRef = FirebaseDatabase.getInstance().getReference("clases")

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 🔹 Obtener las clases activas del estudiante
        obtenerClasesActivas()

        // 🔹 Volver al inicio estudiante
        tvVolver.setOnClickListener {
            startActivity(Intent(this, InicioEst::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun obtenerClasesActivas() {
        clasesRef.orderByChild("estado").equalTo("Activa")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaClases.clear()
                    for (claseSnap in snapshot.children) {
                        val clase = claseSnap.getValue(Clase::class.java)
                        if (clase != null) {
                            listaClases.add(clase)
                        }
                    }

                    if (listaClases.isEmpty()) {
                        Toast.makeText(this@ClasesEstudiante, "No hay clases activas disponibles", Toast.LENGTH_SHORT).show()
                    }

                    clasesAdapter = ClasesAdapter(listaClases)
                    rvClases.adapter = clasesAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ClasesEstudiante, "Error al cargar clases: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
