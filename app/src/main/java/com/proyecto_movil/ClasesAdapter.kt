package com.proyecto_movil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClasesAdapter(private val listaClases: List<Clase>) :
    RecyclerView.Adapter<ClasesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreClase)
        val tvProfesor: TextView = view.findViewById(R.id.tvProfesorClase)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcionClase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_clase_est, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clase = listaClases[position]
        holder.tvNombre.text = clase.nombre
        holder.tvProfesor.text = "Profesor: ${clase.profesor ?: "Desconocido"}"
        holder.tvDescripcion.text = clase.descripcion
    }

    override fun getItemCount() = listaClases.size
}
