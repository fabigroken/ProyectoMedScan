package com.fabigroken.proyectomedscan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicamentoAdapter(
    private var items: List<Medicamento>,
    // Callback cuando se toca "Más detalles"
    private val onItemClick: (Medicamento) -> Unit
) : RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder>() {

    // Actualiza la lista completa cuando Firestore devuelve nuevos datos
    fun actualizarLista(nuevaLista: List<Medicamento>) {
        items = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicamentoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicamento, parent, false)
        return MedicamentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicamentoViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    override fun getItemCount(): Int = items.size

    // ViewHolder que maneja cada tarjeta del historial
    class MedicamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Elementos del layout item_medicamento.xml
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreMedicamento)
        private val tvPrincipio: TextView = itemView.findViewById(R.id.tvPrincipioActivo)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFechaEscaneo)
        private val btnMasDetalles: Button = itemView.findViewById(R.id.btnMasDetalles)

        // Método que rellena los datos del medicamento en la tarjeta
        fun bind(item: Medicamento, onItemClick: (Medicamento) -> Unit) {

            // Nombre del medicamento
            tvNombre.text = item.nombre

            // Principio activo (si está vacío, mostramos "No especificado")
            tvPrincipio.text = "Principio activo: ${item.principioActivo.ifBlank { "No especificado" }}"

            // Fecha del escaneo
            tvFecha.text = "Fecha: ${item.fechaEscaneo}"

            // Botón que abre el detalle del medicamento
            btnMasDetalles.setOnClickListener { onItemClick(item) }
        }
    }
}
