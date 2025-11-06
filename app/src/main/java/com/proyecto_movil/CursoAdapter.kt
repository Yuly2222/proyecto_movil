package com.proyecto_movil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CursoAdapter(private val cursos: List<Curso>) :
    RecyclerView.Adapter<CursoAdapter.CursoViewHolder>() {

    class CursoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvCursoNombre)
        val codigo: TextView = view.findViewById(R.id.tvCursoCodigo)
        val horario: TextView = view.findViewById(R.id.tvCursoHorario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_curso, parent, false)
        return CursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        val curso = cursos[position]
        holder.nombre.text = curso.nombre
        holder.codigo.text = "Código: ${curso.codigo}"
        holder.horario.text = "Horario: ${curso.horario}"
    }

    override fun getItemCount() = cursos.size
}
