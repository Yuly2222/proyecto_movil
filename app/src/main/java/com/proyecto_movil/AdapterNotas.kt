package com.proyecto_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class Nota(val numero: String, val materia: String,
           val descripcion: String, val nota: String)

class NotasAdapter(private val notas: List<Nota>) : RecyclerView.Adapter<NotasAdapter.NotaViewHolder>() {
    class NotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumero: TextView = itemView.findViewById(R.id.tvNumeroNota)
        val tvMateria: TextView = itemView.findViewById(R.id.tvMateria)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvNota: TextView = itemView.findViewById(R.id.tvNota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_notas_item, parent, false)
        return NotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = notas[position]
        holder.tvNumero.text = nota.numero
        holder.tvMateria.text = nota.materia
        holder.tvDescripcion.text = nota.descripcion
        holder.tvNota.text = nota.nota
    }

    override fun getItemCount() = notas.size
}