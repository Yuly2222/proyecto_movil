package com.proyecto_movil

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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

    /** Intenta obtener el grado del estudiante asignado al acudiente y buscar el foro */
    private fun cargarForoPorGradoDeEstudiante() {
        val uidAcudiente = auth.currentUser?.uid ?: return

        db.reference.child("usuarios").child(uidAcudiente).get()
            .addOnSuccessListener { acuSnap ->
                // 1) ¿Tienes denormalizado el grado en el acudiente?
                val gradoDirecto = acuSnap.child("gradoEstudianteAsignado").value
                if (gradoDirecto != null) {
                    val g = normalizarGrado(gradoDirecto)
                    if (g != null) {
                        buscarForoPorGradoFlexible(g)
                        return@addOnSuccessListener
                    }
                }

                // 2) Fallback: leer uid del estudiante y su grado
                val uidEst = acuSnap.child("uidEstudianteAsignado").getValue(String::class.java)
                if (uidEst.isNullOrBlank()) {
                    Toast.makeText(this, "No hay estudiante asignado a este acudiente.", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }

                db.reference.child("usuarios").child(uidEst).get()
                    .addOnSuccessListener { estSnap ->
                        val gradoRaw = estSnap.child("grado").value  // puede ser 10, "10", "10°", "Décimo"
                        val grado = normalizarGrado(gradoRaw)
                        if (grado == null) {
                            Toast.makeText(this, "El estudiante no tiene grado válido.", Toast.LENGTH_LONG).show()
                            return@addOnSuccessListener
                        }
                        buscarForoPorGradoFlexible(grado)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al leer estudiante: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al leer acudiente: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    /** Busca el foro probando por string, por número y con fallback */
    private fun buscarForoPorGradoFlexible(grado: String) {
        // 1) Buscar como string exacto
        db.reference.child("foros")
            .orderByChild("grado")
            .equalTo(grado)
            .get()
            .addOnSuccessListener { snap ->
                if (snap.exists()) {
                    bindForo(snap.children.first(), grado)
                    return@addOnSuccessListener
                }

                // 2) Intentar como número (si la BD lo guardó como 10 numérico)
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
                                // 3) Fallback: traer todos y comparar normalizando
                                fallbackBuscarForo(grado)
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al buscar foro (num): ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    // 3) Fallback directo
                    fallbackBuscarForo(grado)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al buscar foro (str): ${e.message}", Toast.LENGTH_LONG).show()
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
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al leer foros: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun bindForo(foroSnap: DataSnapshot, grado: String) {
        foroId = foroSnap.key
        tituloForo = foroSnap.child("titulo").getValue(String::class.java) ?: "Foro Grado $grado"
        tvTituloForo.text = tituloForo
        escucharMensajes()
    }

    /** Solo lectura: pinta mensajes como “otros” (izquierda) */
    private fun escucharMensajes() {
        val id = foroId ?: return
        val ref = db.reference.child("foros").child(id).child("mensajes")

        ref.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contenedorMensajes.removeAllViews()

                for (msg in snapshot.children) {
                    val texto = msg.child("texto").getValue(String::class.java) ?: ""
                    val remitente = msg.child("remitenteNombre").getValue(String::class.java) ?: "Profesor"
                    agregarBurbuja(texto, remitente)
                }

                scrollMensajes.post { scrollMensajes.fullScroll(ScrollView.FOCUS_DOWN) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ForoAcudiente, "No se pudieron cargar mensajes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun agregarBurbuja(texto: String, remitente: String) {
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
            gravity = Gravity.START
        }

        val burbuja = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = getDrawable(R.drawable.bg_bubble_other)  // gris claro
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

        burbuja.addView(tvRem)
        burbuja.addView(tvTxt)
        fila.addView(burbuja)
        contenedorMensajes.addView(fila)
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    /** Normaliza entradas como 10, "10", "10°", "Décimo" → "10" */
    private fun normalizarGrado(raw: Any?): String? {
        if (raw == null) return null
        val s = when (raw) {
            is Number -> raw.toInt().toString()
            is String -> raw
            else -> raw.toString()
        }.trim()

        // Si ya es dígito(s) con o sin "°"
        val soloDigitos = s.replace("°", "").trim()
        if (soloDigitos.matches(Regex("^[0-9]{1,2}$"))) return soloDigitos

        // Mapear nombres a número (ajusta si usas otros)
        val mapa = mapOf(
            "primero" to "1", "segundo" to "2", "tercero" to "3", "cuarto" to "4",
            "quinto" to "5", "sexto" to "6", "septimo" to "7", "séptimo" to "7",
            "octavo" to "8", "noveno" to "9", "decimo" to "10", "décimo" to "10",
            "undecimo" to "11", "undécimo" to "11"
        )
        val lower = s.lowercase()
        return mapa[lower]
    }
}
