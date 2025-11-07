package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

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

        // Navegación a RegistroActivity
        btnCrearUsuario.setOnClickListener {
            val intent = Intent(this, RegistroAdminActivity::class.java)
            startActivity(intent)
        }

        // Navegación a DashboardActivity
        btnDashboard.setOnClickListener {
            val intent = Intent(this, Clases::class.java)
            startActivity(intent)
        }

        // Navegación a CrearEventoActivity
        btnCrearEvento.setOnClickListener {
            val intent = Intent(this, CrearEventoAdminActivity::class.java)
            startActivity(intent)
        }

        // Navegación a CrearGrupoActivity
        btnCrearGrupo.setOnClickListener {
            val intent = Intent(this, Foro::class.java)
            startActivity(intent)
        }
    }
}
