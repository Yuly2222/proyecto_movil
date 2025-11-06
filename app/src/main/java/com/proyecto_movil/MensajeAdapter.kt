package com.proyecto_movil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MensajeAdapter(private val mensajes: List<Mensaje>) :
    RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder>() {

    class MensajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRemitente: TextView = itemView.findViewById(R.id.tvRemitente)
        val tvPreview: TextView = itemView.findViewById(R.id.tvPreview)
        val tvHora: TextView = itemView.findViewById(R.id.tvHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mensaje, parent, false)
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        val mensaje = mensajes[position]
        holder.tvRemitente.text = mensaje.remitente
        holder.tvPreview.text = mensaje.preview
        holder.tvHora.text = mensaje.hora
    }

    override fun getItemCount() = mensajes.size
}
