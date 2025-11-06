package com.proyecto_movil

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EstudianteNotaAdapter(private val lista: MutableList<EstudianteNota>) :
    RecyclerView.Adapter<EstudianteNotaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreEst)
        val etNota: EditText = view.findViewById(R.id.etNotaEst)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_estudiante_nota, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val estudiante = lista[position]
        holder.tvNombre.text = estudiante.nombre
        holder.etNota.setText(estudiante.nota.toString())

        holder.etNota.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString()
                estudiante.nota = texto.toDoubleOrNull() ?: 0.0
            }
        })
    }

    override fun getItemCount() = lista.size
}
