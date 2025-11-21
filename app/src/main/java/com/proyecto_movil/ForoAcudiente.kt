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

class ForoAcudiente : AppCompatActivity() {

    private lateinit var tvVolver: TextView
    private lateinit var tvTituloForo: TextView
    private lateinit var contenedorMensajes: LinearLayout
    private lateinit var scrollMensajes: ScrollView

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var foroId: String? = null
    private var tituloForo: String = "Foro del Grado"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foro_acudiente)
        supportActionBar?.hide()

        tvVolver = findViewById(R.id.tvVolver)
        tvTituloForo = findViewById(R.id.tvTituloForo)
        contenedorMensajes = findViewById(R.id.contenedorMensajes)
        scrollMensajes = findViewById(R.id.scrollMensajes)

        tvVolver.setOnClickListener {
            startActivity(Intent(this, InicioAcudiente::class.java))
            finish()
        }

        cargarForoPorGradoDeEstudiante()
    }

    private fun cargarForoPorGradoDeEstudiante() {
        val uidAcudiente = auth.currentUser?.uid ?: return

        db.reference.child("usuarios").child(uidAcudiente).get()
            .addOnSuccessListener { acuSnap ->

                val gradoDirecto = acuSnap.child("gradoEstudianteAsignado").value
                if (gradoDirecto != null) {
                    val g = normalizarGrado(gradoDirecto)
                    if (g != null) {
                        buscarForoPorGradoFlexible(g)
                        return@addOnSuccessListener
                    }
                }

                val uidEst = acuSnap.child("uidEstudianteAsignado").getValue(String::class.java)
                if (uidEst.isNullOrBlank()) {
                    Toast.makeText(this, "No hay estudiante asignado a este acudiente.", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                db.reference.child("usuarios").child(uidEst).get()
                    .addOnSuccessListener { estSnap ->
                        val gradoRaw = estSnap.child("grado").value
                        val grado = normalizarGrado(gradoRaw)
                        if (grado == null) {
                            Toast.makeText(this, "El estudiante no tiene grado válido.", Toast.LENGTH_LONG).show()
                            return@addOnSuccessListener
                        }
                        buscarForoPorGradoFlexible(grado)
                    }
            }
    }

    private fun buscarForoPorGradoFlexible(grado: String) {
        db.reference.child("foros")
            .orderByChild("grado")
            .equalTo(grado)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.exists()) {
                    bindForo(snap.children.first(), grado)
                    return@addOnSuccessListener
                }

                val asNum = grado.toDoubleOrNull()
                if (asNum != null) {
                    db.reference.child("foros")
                        .orderByChild("grado")
                        .equalTo(asNum)
                        .get()
                        .addOnSuccessListener { s2 ->
                            if (s2.exists()) {
                                bindForo(s2.children.first(), grado)
                            } else {
                                fallbackBuscarForo(grado)
                            }
                        }
                } else {
                    fallbackBuscarForo(grado)
                }
            }
    }

    private fun fallbackBuscarForo(grado: String) {
        db.reference.child("foros").get()
            .addOnSuccessListener { all ->
                var match: DataSnapshot? = null
                for (f in all.children) {
                    val gRaw = f.child("grado").value
                    val gNorm = normalizarGrado(gRaw)
                    if (gNorm == grado) {
                        match = f
                        break
                    }
                }

                if (match != null) {
                    bindForo(match!!, grado)
                } else {
                    Toast.makeText(this, "No existe un foro para el grado $grado.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun bindForo(foroSnap: DataSnapshot, grado: String) {
        foroId = foroSnap.key
        tituloForo = foroSnap.child("titulo").getValue(String::class.java) ?: "Foro Grado $grado"
        tvTituloForo.text = tituloForo
        escucharMensajes()
    }

    // -------------------------------------------------------------------------
    // 🔥 LECTURA DE MENSAJES + HEADER DE FECHA (WhatsApp)
    // -------------------------------------------------------------------------
    private fun escucharMensajes() {
        val id = foroId ?: return
        val ref = db.reference.child("foros").child(id).child("mensajes")

        ref.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contenedorMensajes.removeAllViews()

                var ultimaFechaMostrada = ""

                for (msg in snapshot.children) {

                    val texto = msg.child("texto").getValue(String::class.java) ?: ""
                    val remitente = msg.child("remitenteNombre").getValue(String::class.java) ?: "Profesor"
                    val hora = msg.child("hora").getValue(String::class.java) ?: ""
                    val fecha = msg.child("fecha").getValue(String::class.java) ?: ""

                    // 🔥 Insertar encabezado de fecha si cambia
                    if (fecha != ultimaFechaMostrada) {
                        insertarHeaderFecha(formatearFecha(fecha))
                        ultimaFechaMostrada = fecha
                    }

                    agregarBurbuja(texto, remitente, hora)
                }

                scrollMensajes.post { scrollMensajes.fullScroll(ScrollView.FOCUS_DOWN) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ForoAcudiente, "No se pudieron cargar mensajes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // -------------------------------------------------------------------------
    // 🔥 FORMATEAR FECHA A ESTILO WHATSAPP
    // -------------------------------------------------------------------------
    private fun formatearFecha(fechaISO: String): String {
        return try {
            val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outFmt = SimpleDateFormat("d MMMM yyyy", Locale("es"))
            outFmt.format(inFmt.parse(fechaISO)!!)
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
    // 🔥 BURBUJAS (solo lectura, estilo “otros”)
    // -------------------------------------------------------------------------
    private fun agregarBurbuja(texto: String, remitente: String, hora: String) {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.START
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
        }

        val burbuja = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = getDrawable(R.drawable.bg_bubble_other)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.85f)
        }

        val tvRem = TextView(this).apply {
            text = remitente
            textSize = 12f
            setTextColor(0xFF6B7280.toInt())
        }

        val tvTxt = TextView(this).apply {
            text = texto
            textSize = 15f
            setTextColor(0xFF111827.toInt())
        }

        val tvHora = TextView(this).apply {
            text = hora
            textSize = 11f
            setTextColor(0xFF9CA3AF.toInt())
            gravity = Gravity.END
        }

        burbuja.addView(tvRem)
        burbuja.addView(tvTxt)
        burbuja.addView(tvHora)
        fila.addView(burbuja)
        contenedorMensajes.addView(fila)
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun normalizarGrado(raw: Any?): String? {
        if (raw == null) return null
        val s = when (raw) {
            is Number -> raw.toInt().toString()
            is String -> raw
            else -> raw.toString()
        }.trim()

        val soloDigitos = s.replace("°", "").trim()
        if (soloDigitos.matches(Regex("^[0-9]{1,2}$"))) return soloDigitos

        val mapa = mapOf(
            "primero" to "1", "segundo" to "2", "tercero" to "3", "cuarto" to "4",
            "quinto" to "5", "sexto" to "6", "septimo" to "7", "séptimo" to "7",
            "octavo" to "8", "noveno" to "9", "decimo" to "10", "décimo" to "10",
            "undecimo" to "11", "undécimo" to "11"
        )

        return mapa[s.lowercase()]
    }
}


