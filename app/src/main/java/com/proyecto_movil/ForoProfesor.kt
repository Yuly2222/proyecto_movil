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
    val timestamp: Long = System.currentTimeMillis(),
    val hora: String? = null,
    val fecha: String? = null
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

    // -------------------------------------------------------------------------
    // 🔥 BOTÓN ENVIAR: guarda texto, remitente, hora y fecha
    // -------------------------------------------------------------------------
    private fun configurarBotonEnviar() {
        btnEnviar.setOnClickListener {
            val texto = editMensaje.text.toString().trim()
            if (texto.isEmpty()) {
                Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (foroId == null) {
                Toast.makeText(this, "No se encontró el foro", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val idMensaje = db.reference.child("foros").child(foroId!!).child("mensajes")
                .push().key ?: return@setOnClickListener

            val mensaje = MensajeForo(
                idMensaje = idMensaje,
                texto = texto,
                remitenteUid = auth.currentUser?.uid,
                remitenteNombre = nombreProfesor,
                timestamp = System.currentTimeMillis(),
                hora = horaActual,
                fecha = fechaActual
            )

            db.reference.child("foros").child(foroId!!).child("mensajes").child(idMensaje)
                .setValue(mensaje)
                .addOnSuccessListener {
                    editMensaje.text.clear()
                    scrollMensajes.postDelayed({
                        scrollMensajes.fullScroll(ScrollView.FOCUS_DOWN)
                    }, 150)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // -------------------------------------------------------------------------
    // 🔥 LECTURA DE MENSAJES + HEADER DE FECHA (estilo WhatsApp)
    // -------------------------------------------------------------------------
    private fun escucharMensajes() {
        if (foroId == null) return
        val ref = db.reference.child("foros").child(foroId!!).child("mensajes")

        ref.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                contenedorMensajes.removeAllViews()
                var ultimaFechaMostrada = ""

                for (msgSnap in snapshot.children) {

                    val texto = msgSnap.child("texto").getValue(String::class.java) ?: ""
                    val remitente = msgSnap.child("remitenteNombre").getValue(String::class.java) ?: "Anónimo"
                    val uid = msgSnap.child("remitenteUid").getValue(String::class.java)
                    val ts = msgSnap.child("timestamp").getValue(Long::class.java) ?: 0L
                    val hora = msgSnap.child("hora").getValue(String::class.java) ?: ""
                    val fecha = msgSnap.child("fecha").getValue(String::class.java) ?: ""

                    // 🔥 Si es una nueva fecha → agregar encabezado
                    if (fecha != ultimaFechaMostrada) {
                        insertarHeaderFecha(formatearFecha(fecha))
                        ultimaFechaMostrada = fecha
                    }

                    agregarBurbujaMensaje(texto, remitente, uid == auth.currentUser?.uid, ts, hora)
                }

                scrollMensajes.post { scrollMensajes.fullScroll(ScrollView.FOCUS_DOWN) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ForoProfesor, "No se pudieron cargar mensajes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // -------------------------------------------------------------------------
    // 🔥 FORMATO DE FECHA (estilo WhatsApp)
    // -------------------------------------------------------------------------
    private fun formatearFecha(fechaISO: String): String {
        return try {
            val sdfIn = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sdfOut = SimpleDateFormat("d MMMM yyyy", Locale("es"))
            val date = sdfIn.parse(fechaISO)
            sdfOut.format(date!!)
        } catch (e: Exception) {
            fechaISO
        }
    }

    private fun insertarHeaderFecha(fecha: String) {
        val tv = TextView(this).apply {
            text = fecha
            textSize = 13f
            setTextColor(0xFF6B7280.toInt())
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, dp(10))
        }
        contenedorMensajes.addView(tv)
    }

    // -------------------------------------------------------------------------
    // 🔥 BURBUJA DE MENSAJE NORMAL
    // -------------------------------------------------------------------------
    private fun agregarBurbujaMensaje(
        texto: String,
        remitente: String,
        esPropio: Boolean,
        timestamp: Long,
        hora: String
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
            background = if (esPropio)
                getDrawable(R.drawable.bg_bubble_me)
            else
                getDrawable(R.drawable.bg_bubble_other)

            setPadding(dp(12), dp(8), dp(12), dp(8))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f)
        }

        val tvRem = TextView(this).apply {
            text = remitente
            textSize = 12f
            setTextColor(
                if (esPropio) 0xFFFFFFFF.toInt()
                else 0xFF6B7280.toInt()
            )
        }

        val tvTxt = TextView(this).apply {
            text = texto
            textSize = 15f
            setTextColor(
                if (esPropio) 0xFFFFFFFF.toInt()
                else 0xFF111827.toInt()
            )
        }

        val tvHora = TextView(this).apply {
            text = hora
            textSize = 11f
            setTextColor(
                if (esPropio) 0xFFE5E7EB.toInt()
                else 0xFF9CA3AF.toInt()
            )
            gravity = Gravity.END
        }

        burbuja.addView(tvRem)
        burbuja.addView(tvTxt)
        burbuja.addView(tvHora)
        fila.addView(burbuja)
        contenedorMensajes.addView(fila)
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}

