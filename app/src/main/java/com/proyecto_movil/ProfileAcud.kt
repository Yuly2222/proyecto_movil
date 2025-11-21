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

class ProfileAcud : AppCompatActivity() {

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private var userListener: ValueEventListener? = null

    // UI
    private lateinit var ivAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRol: TextView
    private lateinit var tvHijos: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_acud)
        supportActionBar?.hide()

        // Firebase
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            goToLogin(); return
        }

        db = FirebaseDatabase.getInstance().reference
            .child("usuarios")
            .child(user.uid)

        // UI
        ivAvatar = findViewById(R.id.ivAvatarAcud)
        tvName  = findViewById(R.id.tvNameAcud)
        tvEmail = findViewById(R.id.tvEmailAcud)
        tvRol   = findViewById(R.id.tvRolAcud)
        tvHijos = findViewById(R.id.tvHijos)
        btnEdit = findViewById(R.id.btnEditAcud)
        btnLogout = findViewById(R.id.btnLogoutAcud)

        // ------------------------------
        // 🔹 LÓGICA CORRECTA PARA CARGAR HIJO ASIGNADO
        // ------------------------------
        userListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val nombre = snapshot.child("nombre").getValue(String::class.java)
                val correo = snapshot.child("email").getValue(String::class.java)
                val rol = snapshot.child("tipoUsuario").getValue(String::class.java)

                // Obtener UID del estudiante asignado
                val uidEstudiante = snapshot.child("uidEstudianteAsignado").getValue(String::class.java)

                tvName.text = nombre ?: "Acudiente"
                tvEmail.text = correo ?: "sin correo"
                tvRol.text = rol ?: "Acudiente"

                if (uidEstudiante != null) {
                    cargarDatosEstudiante(uidEstudiante)
                } else {
                    tvHijos.text = "Hijos asociados: No registrados"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ProfileAcud,
                    "Error cargando datos: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        db.addValueEventListener(userListener!!)

        // Botones
        btnEdit.setOnClickListener {
            Toast.makeText(this, "Editar perfil (pendiente)", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            logoutClean()
        }

        // Bottom Navbar
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_profile

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, InicioAcudiente::class.java)); true }
                R.id.nav_calendar -> { startActivity(Intent(this, Calendario::class.java)); true }
                R.id.nav_notifications -> { startActivity(Intent(this, Comunicados::class.java)); true }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    // 🔹 FUNCIÓN PARA CARGAR EL ESTUDIANTE DESDE "usuarios/uid"
    private fun cargarDatosEstudiante(uid: String) {
        val ref = FirebaseDatabase.getInstance().reference
            .child("usuarios")
            .child(uid)

        ref.get().addOnSuccessListener { snap ->
            if (snap.exists()) {
                val nombreH = snap.child("nombre").getValue(String::class.java) ?: "Sin nombre"
                val gradoH = snap.child("grado").getValue(String::class.java) ?: "Sin grado"

                tvHijos.text = "Hijo asignado:\n• $nombreH (Grado $gradoH)"
            } else {
                tvHijos.text = "Hijo asignado: No encontrado"
            }
        }.addOnFailureListener {
            tvHijos.text = "Hijo asignado: Error al cargar"
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

    private fun logoutClean() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        FirebaseDatabase.getInstance().goOffline()
        FirebaseDatabase.getInstance().goOnline()

        val prefs = getSharedPreferences("USER_DATA", MODE_PRIVATE)
        prefs.edit().clear().apply()

        val i = Intent(this, Login::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        finish()
    }
}



