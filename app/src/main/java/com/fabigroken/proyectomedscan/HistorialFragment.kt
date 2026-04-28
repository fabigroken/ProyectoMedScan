package com.fabigroken.proyectomedscan

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistorialFragment : Fragment(R.layout.fragment_historial) {

    private lateinit var repository: MedicamentoRepository
    private lateinit var adapter: MedicamentoAdapter
    private lateinit var rvHistorial: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = MedicamentoRepository()
        rvHistorial = view.findViewById(R.id.rvHistorial)
        tvEmpty = view.findViewById(R.id.tvEmptyHistorial)

        adapter = MedicamentoAdapter(emptyList()) { medicamento ->
            findNavController().navigate(
                R.id.action_historialFragment_to_detalleFragment,
                bundleOf("medicamentoId" to medicamento.id)
            )
        }

        rvHistorial.layoutManager = LinearLayoutManager(requireContext())
        rvHistorial.adapter = adapter

        cargarHistorial()
    }

    override fun onResume() {
        super.onResume()
        cargarHistorial()
    }

    private fun cargarHistorial() {
        repository.obtenerHistorial(
            onSuccess = { lista ->
                adapter.actualizarLista(lista)
                tvEmpty.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                rvHistorial.visibility = if (lista.isEmpty()) View.GONE else View.VISIBLE
            },
            onError = { e ->
                Toast.makeText(requireContext(), "Error al cargar historial: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}

