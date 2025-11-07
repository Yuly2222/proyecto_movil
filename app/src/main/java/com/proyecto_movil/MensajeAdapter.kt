package com.proyecto_movil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MensajeAdapter(
    private val mensajes: List<Mensaje>,
    private val uidActual: String?
) : RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder>() {

    class MensajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardMensaje)
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

        // Si el mensaje es del usuario actual → fondo verde suave
        val colorFondo = if (mensaje.uid == uidActual) {
            android.graphics.Color.parseColor("#D0F8CE") // verde suave
        } else {
            android.graphics.Color.WHITE
        }

        holder.cardView.setCardBackgroundColor(colorFondo)
    }

    override fun getItemCount() = mensajes.size
}
