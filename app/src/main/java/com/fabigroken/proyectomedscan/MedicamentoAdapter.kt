package com.fabigroken.proyectomedscan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicamentoAdapter(
    private var items: List<Medicamento>,
    private val onItemClick: (Medicamento) -> Unit
) : RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder>() {

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

    class MedicamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreMedicamento)
        private val tvPrincipio: TextView = itemView.findViewById(R.id.tvPrincipioActivo)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFechaEscaneo)
        private val btnMasDetalles: Button = itemView.findViewById(R.id.btnMasDetalles)

        fun bind(item: Medicamento, onItemClick: (Medicamento) -> Unit) {
            tvNombre.text = item.nombre
            tvPrincipio.text = "Principio activo: ${item.principioActivo}"
            tvFecha.text = "Fecha: ${item.fechaEscaneo}"
            btnMasDetalles.setOnClickListener { onItemClick(item) }
        }
    }
}

