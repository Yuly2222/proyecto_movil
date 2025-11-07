package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CursosProf : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var rvCursos: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var btnAgregarCurso: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cursos_prof)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        rvCursos = findViewById(R.id.rvCursos)
        rvCursos.layoutManager = LinearLayoutManager(this)

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_courses
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, InicioProf::class.java))
                    true
                }
                R.id.nav_courses -> true
                R.id.nav_calendar -> {
                    startActivity(Intent(this, Calendario_Profe::class.java))
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, Comunicados::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileProfe::class.java))
                    true
                }
                else -> false
            }
        }

        btnAgregarCurso = findViewById(R.id.btnAgregarCurso)
        btnAgregarCurso.setOnClickListener {
            // Abrir la actividad CrearClase
            startActivity(Intent(this, CrearClase::class.java))
        }

        cargarCursosProfesor()
    }

    private fun cargarCursosProfesor() {
        val user = auth.currentUser ?: return
        val uidProfesor = user.uid

        val clasesRef = database.getReference("clases")
        clasesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaCursos = mutableListOf<Curso>()

                for (claseSnap in snapshot.children) {
                    val uid = claseSnap.child("uidProfesor").getValue(String::class.java)
                    if (uid == uidProfesor) {
                        val nombre = claseSnap.child("nombre").getValue(String::class.java) ?: ""
                        val idClase = claseSnap.child("idClase").getValue(String::class.java) ?: ""
                        val horario = claseSnap.child("descripcion").getValue(String::class.java) ?: ""

                        listaCursos.add(Curso(nombre, idClase, horario))
                    }
                }

                if (listaCursos.isEmpty()) {
                    Toast.makeText(
                        this@CursosProf,
                        "No tienes clases registradas aún.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // 🔹 Aquí se asigna el adapter, ahora cada item es clickeable
                rvCursos.adapter = CursoAdapter(listaCursos)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@CursosProf,
                    "Error al cargar clases: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
