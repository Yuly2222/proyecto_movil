package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileProfe : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private var userListener: ValueEventListener? = null

    // UI
    private lateinit var ivAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRol: TextView
    private lateinit var tvGrado: TextView
    private lateinit var tvMaterias: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_profe)

        // 🔹 Firebase
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            goToLogin(); return
        }

        db = FirebaseDatabase.getInstance().reference
            .child("profesores_colegio")
            .child(user.uid)

        // 🔹 UI
        ivAvatar = findViewById(R.id.ivAvatarProfe)
        tvName = findViewById(R.id.tvNameProfe)
        tvEmail = findViewById(R.id.tvEmailProfe)
        tvRol = findViewById(R.id.tvRolProfe)
        tvGrado = findViewById(R.id.tvGrado)
        tvMaterias = findViewById(R.id.tvMaterias)
        btnEdit = findViewById(R.id.btnEditProfe)
        btnLogout = findViewById(R.id.btnLogoutProfe)

        // 🔹 Escuchar datos desde Firebase
        userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombre = snapshot.child("nombre").getValue(String::class.java)
                val correo = snapshot.child("correo").getValue(String::class.java)
                val rol = snapshot.child("rol").getValue(String::class.java)
                val grado = snapshot.child("grado").getValue(String::class.java)
                val materias = snapshot.child("materias").getValue(String::class.java)

                tvName.text = nombre ?: user.displayName ?: "Profesor"
                tvEmail.text = correo ?: user.email ?: "sin correo"
                tvRol.text = rol ?: "Profesor de Colegio"
                tvGrado.text = "Grado asignado: ${grado ?: "No asignado"}"
                tvMaterias.text = "Materias: ${materias ?: "No registradas"}"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ProfileProfe,
                    "Error cargando datos: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        db.addValueEventListener(userListener!!)

        // 🔹 Botones
        btnEdit.setOnClickListener {
            Toast.makeText(this, "Editar perfil (pendiente)", Toast.LENGTH_SHORT).show()
        }
        btnLogout.setOnClickListener {
            auth.signOut(); goToLogin()
        }

        // 🔹 Barra inferior
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_profile
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, InicioProf::class.java)); true }
                R.id.nav_courses -> { startActivity(Intent(this, cursos_prof::class.java)); true }
                R.id.nav_calendar -> { startActivity(Intent(this, Calendario::class.java)); true }
                R.id.nav_notifications -> { startActivity(Intent(this, Comunicados::class.java)); true }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userListener?.let { db.removeEventListener(it) }
    }

    private fun goToLogin() {
        startActivity(Intent(this, Login::class.java))
        finish()
    }
}
