package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Foro : AppCompatActivity() {

    private lateinit var recyclerMensajes: RecyclerView
    private lateinit var editMensaje: EditText
    private lateinit var btnEnviar: ImageView
    private lateinit var btnVolver: TextView

    private lateinit var dbRef: DatabaseReference
    private val listaMensajes = mutableListOf<Mensaje>()
    private lateinit var adapter: MensajeAdapter

    private val usuarioActual = FirebaseAuth.getInstance().currentUser
    private var tipoUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foro)

        recyclerMensajes = findViewById(R.id.recyclerMensajes)
        editMensaje = findViewById(R.id.editMensaje)
        btnEnviar = findViewById(R.id.btnEnviar)
        btnVolver = findViewById(R.id.btnVolver)

        // Recuperar tipo de usuario desde Intent
        tipoUsuario = intent.getStringExtra("tipoUsuario")

        recyclerMensajes.layoutManager = LinearLayoutManager(this)
        adapter = MensajeAdapter(listaMensajes, usuarioActual?.uid)
        recyclerMensajes.adapter = adapter

        dbRef = FirebaseDatabase.getInstance().getReference("mensajes")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaMensajes.clear()
                for (mensajeSnapshot in snapshot.children) {
                    val mensaje = mensajeSnapshot.getValue(Mensaje::class.java)
                    if (mensaje != null) listaMensajes.add(mensaje)
                }
                adapter.notifyDataSetChanged()
                recyclerMensajes.scrollToPosition(listaMensajes.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        btnEnviar.setOnClickListener {
            val texto = editMensaje.text.toString().trim()
            if (texto.isNotEmpty()) {
                val hora = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date())

                val nuevoMensaje = Mensaje(
                    remitente = usuarioActual?.email ?: "Anónimo",
                    preview = texto,
                    hora = hora,
                    uid = usuarioActual?.uid
                )
                dbRef.push().setValue(nuevoMensaje)
                editMensaje.text.clear()
            }
        }

        // === Acción del botón Volver ===
        btnVolver.setOnClickListener {
            when (tipoUsuario) {
                "admin" -> startActivity(Intent(this, InicioAdminActivity::class.java))
                "profesor" -> startActivity(Intent(this, InicioProf::class.java))
                "acudiente" -> startActivity(Intent(this, InicioAcudiente::class.java))
                "estudiante" -> startActivity(Intent(this, InicioEst::class.java))
                else -> finish()
            }
            finish()
        }
    }
}
