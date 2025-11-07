package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Comunicados : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var userRol: String = "Desconocido"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imicio_comunicados)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_notifications

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // 🔹 Obtener rol del usuario
        database.getReference("usuarios").child(user.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userRol = snapshot.child("tipoUsuario").getValue(String::class.java) ?: "Desconocido"
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Comunicados, "Error al obtener rol del usuario", Toast.LENGTH_SHORT).show()
                }
            })

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val destino: Class<out AppCompatActivity> = when (userRol) {
                        "Admin" -> InicioAdminActivity::class.java
                        "Profesor" -> InicioProf::class.java
                        "Estudiante" -> InicioEst::class.java
                        "Acudiente" -> InicioAcudiente::class.java
                        else -> MainActivity::class.java
                    }
                    startActivity(Intent(this@Comunicados, destino))
                    true
                }

                R.id.nav_courses -> {
                    val destino: Class<out AppCompatActivity> = when (userRol) {
                        "Admin" -> VerClasesActivas::class.java
                        "Profesor" -> CursosProf::class.java
                        "Estudiante" -> NotasEst::class.java
                        "Acudiente" -> NotasEst::class.java
                        else -> MainActivity::class.java
                    }
                    startActivity(Intent(this@Comunicados, destino))
                    true
                }

                R.id.nav_calendar -> {
                    startActivity(Intent(this@Comunicados, Calendario::class.java))
                    true
                }

                R.id.nav_notifications -> true

                R.id.nav_profile -> {
                    val destino: Class<out AppCompatActivity> = when (userRol) {
                        "Admin" -> ProfileProfe::class.java
                        "Profesor" -> ProfileProfe::class.java
                        "Estudiante" -> ProfileActivityEst::class.java
                        "Acudiente" -> ProfileActivityEst::class.java
                        else -> MainActivity::class.java
                    }
                    startActivity(Intent(this@Comunicados, destino))
                    true
                }

                else -> false
            }
        }

        // 🔹 RecyclerView de comunicados
        val rvComunicados = findViewById<RecyclerView>(R.id.rvComunicados)
        rvComunicados.layoutManager = LinearLayoutManager(this)

        val listaComunicados = listOf(
            Comunicado(R.drawable.estrella, "Juan", "10:00 a.m.", "Título 1", "Mensaje 1..."),
            Comunicado(R.drawable.estrella, "Ana", "11:30 a.m.", "Título 2", "Mensaje 2...")
        )

        rvComunicados.adapter = AdapterComunicados(listaComunicados)
    }
}
