package com.proyecto_movil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proyecto_movil.R
import com.proyecto_movil.Evento

class EventoAdapter : RecyclerView.Adapter<EventoAdapter.VH>() {

    private val items = mutableListOf<Evento>()

    fun submit(list: List<Evento>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvTitulo = v.findViewById<TextView>(R.id.tvTitulo)
        private val tvDetalle = v.findViewById<TextView>(R.id.tvDetalle)
        private val tvHora = v.findViewById<TextView>(R.id.tvHora)

        fun bind(e: Evento) {
            tvTitulo.text = e.titulo
            tvDetalle.text = e.detalle
            tvHora.text = e.hora
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evento, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
