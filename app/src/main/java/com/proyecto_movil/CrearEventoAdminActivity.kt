package com.proyecto_movil

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.util.*

class CrearEventoAdminActivity : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_evento_admin)

        supportActionBar?.hide()

        db = FirebaseDatabase.getInstance()

        // Referencias a vistas
        val txtVolver = findViewById<TextView>(R.id.txtVolver)
        val editTitulo = findViewById<EditText>(R.id.editTitulo)
        val editDetalle = findViewById<EditText>(R.id.editDetalle)
        val editFecha = findViewById<EditText>(R.id.editFecha)
        val editHora = findViewById<EditText>(R.id.editHora)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        txtVolver.setTextColor(Color.WHITE)
        btnGuardar.setTextColor(Color.WHITE)

        // --- Selector de Fecha ---
        editFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val year = calendario.get(Calendar.YEAR)
            val month = calendario.get(Calendar.MONTH)
            val day = calendario.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                val fechaStr = String.format("%04d-%02d-%02d", y, m + 1, d)
                editFecha.setText(fechaStr)
            }, year, month, day).show()
        }

        // --- Selector de Hora ---
        editHora.setOnClickListener {
            val calendario = Calendar.getInstance()
            val hour = calendario.get(Calendar.HOUR_OF_DAY)
            val minute = calendario.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, h, m ->
                val horaStr = String.format("%02d:%02d", h, m)
                editHora.setText(horaStr)
            }, hour, minute, true).show()
        }

        // Acción del texto "Volver"
        txtVolver.setOnClickListener {
            finish()
        }

        // Acción del botón "Guardar Evento"
        btnGuardar.setOnClickListener {
            val titulo = editTitulo.text.toString().trim()
            val detalle = editDetalle.text.toString().trim()
            val fechaStr = editFecha.text.toString().trim()
            val hora = editHora.text.toString().trim()

            // Validaciones
            if (titulo.isEmpty() || detalle.isEmpty() || fechaStr.isEmpty() || hora.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val fecha = LocalDate.parse(fechaStr)
                val fechaEpoch = fecha.toEpochDay()

                val evento = Evento(
                    fechaEpochDay = fechaEpoch,
                    titulo = titulo,
                    detalle = detalle,
                    hora = hora
                )

                val eventosRef = db.reference.child("eventos")
                val nuevoEventoRef = eventosRef.push()

                nuevoEventoRef.setValue(evento)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Evento creado correctamente", Toast.LENGTH_LONG).show()
                        editTitulo.text.clear()
                        editDetalle.text.clear()
                        editFecha.text.clear()
                        editHora.text.clear()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar evento: ${it.message}", Toast.LENGTH_LONG).show()
                    }

            } catch (e: Exception) {
                Toast.makeText(this, "Formato de fecha inválido (use YYYY-MM-DD)", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this, InicioAdminActivity::class.java)
            startActivity(intent)
        }
    }
}
