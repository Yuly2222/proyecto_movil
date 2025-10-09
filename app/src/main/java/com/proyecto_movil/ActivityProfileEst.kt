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

class ProfileActivityEst : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private var userListener: ValueEventListener? = null

    // UI
    private lateinit var ivAvatar: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvRol: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.bottom_navigation_view)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_est)

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
        ivAvatar = findViewById(R.id.ivAvatar)
        tvName   = findViewById(R.id.tvName)
        tvEmail  = findViewById(R.id.tvEmail)
        tvRol    = findViewById(R.id.tvRolEst)
        btnEdit  = findViewById(R.id.btnEdit)
        btnLogout= findViewById(R.id.btnLogout)

        // Cargar datos desde DB (y fallback a Auth)
        userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombreDb = snapshot.child("nombre").getValue(String::class.java)
                val correoDb = snapshot.child("correo").getValue(String::class.java)
                val rolDb    = snapshot.child("rol").getValue(String::class.java)

                tvName.text  = when {
                    !nombreDb.isNullOrBlank() -> nombreDb
                    !user.displayName.isNullOrBlank() -> user.displayName
                    else -> "Usuario"
                }
                tvEmail.text = when {
                    !correoDb.isNullOrBlank() -> correoDb
                    !user.email.isNullOrBlank() -> user.email
                    else -> "sin correo"
                }
                if (!rolDb.isNullOrBlank()) tvRol.text = rolDb
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivityEst,
                    "Error cargando perfil: ${error.message}", Toast.LENGTH_SHORT).show()
                // Fallback mínimo a Auth si falla DB
                tvName.text = user.displayName ?: "Usuario"
                tvEmail.text = user.email ?: "sin correo"
            }
        }
        db.addValueEventListener(userListener!!)

        // Editar / Logout
        btnEdit.setOnClickListener {
            Toast.makeText(this, "Editar perfil (pendiente)", Toast.LENGTH_SHORT).show()
        }
        btnLogout.setOnClickListener {
            auth.signOut(); goToLogin()
        }

        // BottomNav (marca Perfil)
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_profile
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, InicioEst::class.java)); true }
                R.id.nav_courses -> { startActivity(Intent(this, NotasEst::class.java)); true }
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
