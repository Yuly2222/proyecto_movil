package com.proyecto_movil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AsistenciaAdapter(private val estudiantes: List<Estudiante>) :
    RecyclerView.Adapter<AsistenciaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreEstudiante)
        val check: CheckBox = view.findViewById(R.id.cbPresente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_estudiante_asistencia, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = estudiantes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val estudiante = estudiantes[position]
        holder.nombre.text = estudiante.nombre
        holder.check.isChecked = estudiante.presente

        holder.check.setOnCheckedChangeListener { _, isChecked ->
            estudiante.presente = isChecked
        }
    }
}
