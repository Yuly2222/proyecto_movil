package com.proyecto_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class AdapterComunicados(
    private val comunicados: List<Comunicado>
) : RecyclerView.Adapter<AdapterComunicados.ComunicadoViewHolder>() {

    inner class ComunicadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivEmisorAvatar: ImageView = itemView.findViewById(R.id.ivEmisorAvatar)
        val tvEmisorNombre: TextView = itemView.findViewById(R.id.tvEmisorNombre)
        val tvHoraComunicado: TextView = itemView.findViewById(R.id.tvHoraComunicado)
        val tvComunicadoTitulo: TextView = itemView.findViewById(R.id.tvComunicadoTitulo)
        val tvComunicadoPreview: TextView = itemView.findViewById(R.id.tvComunicadoPreview)
        val ivAccionSecundaria: ImageView = itemView.findViewById(R.id.ivAccionSecundaria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComunicadoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_comunicados, parent, false)
        return ComunicadoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComunicadoViewHolder, position: Int) {
        val comunicado = comunicados[position]
        holder.ivEmisorAvatar.setImageResource(comunicado.emisorAvatarRes)
        holder.tvEmisorNombre.text = comunicado.emisorNombre
        holder.tvHoraComunicado.text = comunicado.hora
        holder.tvComunicadoTitulo.text = comunicado.titulo
        holder.tvComunicadoPreview.text = comunicado.preview
        // Puedes configurar ivAccionSecundaria si lo necesitas
    }

    override fun getItemCount(): Int = comunicados.size
}
