package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class InicioAdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio_admin)

        // Ocultar la barra superior (opcional)
        supportActionBar?.hide()

        // Referencias a los ImageView dentro de las CardView
        val btnCrearUsuario = findViewById<ImageView>(R.id.crearUsuario)
        val btnDashboard = findViewById<ImageView>(R.id.dashboard)
        val btnCrearEvento = findViewById<ImageView>(R.id.crearEvento)
        val btnCrearGrupo = findViewById<ImageView>(R.id.foro)
        val btnLogout = findViewById<TextView>(R.id.btnLogout)

        // Navegación a RegistroActivity
        btnCrearUsuario.setOnClickListener {
            val intent = Intent(this, RegistroAdminActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, Bienvenida::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Navegación a DashboardActivity
        btnDashboard.setOnClickListener {
            val intent = Intent(this, CrearClase::class.java)
            startActivity(intent)
        }

        // Navegación a CrearEventoActivity
        btnCrearEvento.setOnClickListener {
            val intent = Intent(this, CrearEventoAdminActivity::class.java)
            startActivity(intent)
        }

        // Navegación a CrearGrupoActivity
        btnCrearGrupo.setOnClickListener {
            val intent = Intent(this, CrearForoAdmin::class.java)
            startActivity(intent)
        }
    }
}
