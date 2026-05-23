package com.fabigroken.proyectomedscan

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistorialFragment : Fragment(R.layout.fragment_historial) {

    /** Repositorio que maneja Firestore */
    private lateinit var repository: MedicamentoRepository

    /** Adaptador del RecyclerView */
    private lateinit var adapter: MedicamentoAdapter

    /** Lista y texto de vacio */
    private lateinit var rvHistorial: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** Botón para volver atrás */
        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        /** Inicializamos el repositorio */
        repository = MedicamentoRepository()

        /** Conectamos los elementos del layout */
        rvHistorial = view.findViewById(R.id.rvHistorial)
        tvEmpty = view.findViewById(R.id.tvEmptyHistorial)

        /** Adaptador del historial */
        /** Cuando se toca un medicamento, se abre el detalle */
        adapter = MedicamentoAdapter(emptyList()) { medicamento ->
            findNavController().navigate(
                R.id.action_historialFragment_to_detalleFragment,
                bundleOf("medicamentoId" to medicamento.id)
            )
        }

        /** Configuración del RecyclerView */
        rvHistorial.layoutManager = LinearLayoutManager(requireContext())
        rvHistorial.adapter = adapter

        /** Cargamos el historial al entrar */
        cargarHistorial()
    }

    override fun onResume() {
        super.onResume()
        /** Cada vez que volvemos aquí, recargamos la lista */
        cargarHistorial()
    }

    /** Método que obtiene la lista de medicamentos desde Firestore */
    private fun cargarHistorial() {

        repository.obtenerHistorial(
            onSuccess = { lista ->

                /** Actualizamos la lista del adaptador */
                adapter.actualizarLista(lista)

                /** Si no hay medicamentos, mostramos el texto de vacío */
                tvEmpty.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE

                /** Y ocultamos o mostramos la lista según corresponda */
                rvHistorial.visibility = if (lista.isEmpty()) View.GONE else View.VISIBLE
            },
            onError = { e ->
                Toast.makeText(
                    requireContext(),
                    "Error al cargar historial: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}
