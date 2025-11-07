package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

data class MensajeForo(
    val idMensaje: String? = null,
    val texto: String? = null,
    val remitenteUid: String? = null,
    val remitenteNombre: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

class ForoProfesor : AppCompatActivity() {

    private lateinit var tvVolver: TextView
    private lateinit var tvTituloForo: TextView
    private lateinit var editMensaje: EditText
    private lateinit var btnEnviar: ImageButton
    private lateinit var contenedorMensajes: LinearLayout
    private lateinit var scrollMensajes: ScrollView

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var foroId: String? = null
    private var foroGrado: String? = null
    private var nombreProfesor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foro_profesor)
        supportActionBar?.hide()

        tvVolver = findViewById(R.id.tvVolver)
        tvTituloForo = findViewById(R.id.tvTituloForo)
        editMensaje = findViewById(R.id.editMensaje)
        btnEnviar = findViewById(R.id.btnEnviar)
        contenedorMensajes = findViewById(R.id.contenedorMensajes)
        scrollMensajes = findViewById(R.id.scrollMensajes)

        // Evita enviar sin foro cargado
        btnEnviar.isEnabled = false

        tvVolver.setOnClickListener {
            startActivity(Intent(this, InicioProf::class.java))
            finish()
        }

        obtenerDatosProfesor()
    }

    private fun obtenerDatosProfesor() {
        val uid = auth.currentUser?.uid ?: return
        db.reference.child("usuarios").child(uid).get()
            .addOnSuccessListener { snapshot ->
                nombreProfesor = snapshot.child("nombre").getValue(String::class.java)
                foroGrado = snapshot.child("directorCurso").getValue(String::class.java)
                if (foroGrado != null) {
                    val gradoNormalizado = foroGrado!!.replace("°", "").trim()
                    buscarForoPorGrado(gradoNormalizado)
                } else {
                    Toast.makeText(this, "No se encontró un grado asignado.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos del profesor", Toast.LENGTH_SHORT).show()
            }
    }

    private fun buscarForoPorGrado(gradoNormalizado: String) {
        val uid = auth.currentUser?.uid ?: return

        // 1) Buscar por uidJefe (recomendado, directo)
        db.reference.child("foros")
            .orderByChild("uidJefe")
            .equalTo(uid)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.exists()) {
                    val foroSnap = snap.children.first()
                    foroId = foroSnap.key
                    setForoUi(foroSnap.child("titulo").getValue(String::class.java) ?: "Foro del Grado")
                    return@addOnSuccessListener
                }

                // 2) Fallback: buscar por grado (p.ej. "10")
                db.reference.child("foros")
                    .orderByChild("grado")
                    .equalTo(gradoNormalizado)
                    .get()
                    .addOnSuccessListener { s2 ->
                        if (s2.exists()) {
                            val foroSnap = s2.children.first()
                            foroId = foroSnap.key
                            setForoUi(foroSnap.child("titulo").getValue(String::class.java) ?: "Foro Grado $gradoNormalizado")
                        } else {
                            Toast.makeText(this, "No existe foro para el grado $gradoNormalizado", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al buscar foro (grado): ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al buscar foro (uidJefe): ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setForoUi(titulo: String) {
        tvTituloForo.text = titulo
        escucharMensajes()
        configurarBotonEnviar()
        btnEnviar.isEnabled = true
    }

    private fun configurarBotonEnviar() {
        btnEnviar.setOnClickListener {
            val texto = editMensaje.text.toString().trim()
            if (texto.isEmpty()) {
                Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (foroId == null) {
                Toast.makeText(this, "No se encontró el foro, recarga la pantalla", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val idMensaje = db.reference.child("foros").child(foroId!!).child("mensajes").push().key ?: return@setOnClickListener
            val mensaje = MensajeForo(
                idMensaje = idMensaje,
                texto = texto,
                remitenteUid = auth.currentUser?.uid,
                remitenteNombre = nombreProfesor
            )

            db.reference.child("foros").child(foroId!!).child("mensajes").child(idMensaje).setValue(mensaje)
                .addOnSuccessListener {
                    editMensaje.text.clear()
                    Toast.makeText(this, "Mensaje enviado ✅", Toast.LENGTH_SHORT).show()
                    scrollMensajes.postDelayed({
                        scrollMensajes.fullScroll(ScrollView.FOCUS_DOWN)
                    }, 150)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /** Lee mensajes ordenados por timestamp y los pinta como burbujas */
    private fun escucharMensajes() {
        if (foroId == null) return
        val ref = db.reference.child("foros").child(foroId!!).child("mensajes")

        ref.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contenedorMensajes.removeAllViews()

                for (msgSnap in snapshot.children) {
                    val texto = msgSnap.child("texto").getValue(String::class.java) ?: ""
                    val remitente = msgSnap.child("remitenteNombre").getValue(String::class.java) ?: "Anónimo"
                    val uid = msgSnap.child("remitenteUid").getValue(String::class.java)
                    val ts = msgSnap.child("timestamp").getValue(Long::class.java) ?: 0L

                    agregarBurbujaMensaje(texto, remitente, uid == auth.currentUser?.uid, ts)
                }

                scrollMensajes.post { scrollMensajes.fullScroll(ScrollView.FOCUS_DOWN) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ForoProfesor, "No se pudieron cargar mensajes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /** Dibuja una burbuja alineada según sea propio/ajeno */
    private fun agregarBurbujaMensaje(
        texto: String,
        remitente: String,
        esPropio: Boolean,
        timestamp: Long
    ) {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
            gravity = if (esPropio) Gravity.END else Gravity.START
        }

        val burbuja = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = if (esPropio) getDrawable(R.drawable.bg_bubble_me) else getDrawable(R.drawable.bg_bubble_other)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f)
        }

        val tvRem = TextView(this).apply {
            text = remitente
            textSize = 12f
            setTextColor(if (esPropio) 0xFFFFFFFF.toInt() else 0xFF6B7280.toInt()) // blanco / gris
        }

        val tvTxt = TextView(this).apply {
            text = texto
            textSize = 15f
            setTextColor(if (esPropio) 0xFFFFFFFF.toInt() else 0xFF111827.toInt()) // blanco / gris oscuro
        }

        burbuja.addView(tvRem)
        burbuja.addView(tvTxt)
        fila.addView(burbuja)
        contenedorMensajes.addView(fila)
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}

