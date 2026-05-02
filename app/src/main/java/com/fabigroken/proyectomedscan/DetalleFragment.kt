package com.fabigroken.proyectomedscan

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class DetalleFragment : Fragment(R.layout.fragment_detalle) {

    private val repository = MedicamentoRepository()
    private var medicamentoActual: Medicamento? = null

    // Campos del layout
    private lateinit var etId: EditText
    private lateinit var etNombre: EditText
    private lateinit var etPrincipioActivo: EditText
    private lateinit var etFrecuencia: EditText
    private lateinit var etParaQueSirve: EditText
    private lateinit var etContraindicaciones: EditText
    private lateinit var etEfectosSecundarios: EditText
    private lateinit var etAdvertencias: EditText
    private lateinit var etReceta: EditText
    private lateinit var etLaboratorio: EditText

    // Solo queda el botón eliminar
    private lateinit var btnEliminar: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón volver
        view.findViewById<Button>(R.id.btnVolver).setOnClickListener {
            findNavController().popBackStack()
        }

        // Conectar campos
        etId = view.findViewById(R.id.etId)
        etNombre = view.findViewById(R.id.etNombre)
        etPrincipioActivo = view.findViewById(R.id.etPrincipioActivo)
        etFrecuencia = view.findViewById(R.id.etFrecuencia)
        etParaQueSirve = view.findViewById(R.id.etParaQueSirve)
        etContraindicaciones = view.findViewById(R.id.etContraindicaciones)
        etEfectosSecundarios = view.findViewById(R.id.etEfectosSecundarios)
        etAdvertencias = view.findViewById(R.id.etAdvertencias)
        etReceta = view.findViewById(R.id.etReceta)
        etLaboratorio = view.findViewById(R.id.etLaboratorio)

        btnEliminar = view.findViewById(R.id.btnEliminar)

        // Obtener ID del medicamento
        val id = arguments?.getString("medicamentoId")

        if (id.isNullOrBlank()) {
            Toast.makeText(requireContext(), "ID no válido", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        // Cargar datos desde Firestore
        cargarMedicamento(id)

        // Botón eliminar
        btnEliminar.setOnClickListener {
            val med = medicamentoActual ?: return@setOnClickListener

            repository.eliminarMedicamento(
                id = med.id,
                onSuccess = {
                    Toast.makeText(requireContext(), "Medicamento eliminado", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                },
                onError = { e ->
                    Toast.makeText(requireContext(), "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    // Cargar datos desde Firestore
    private fun cargarMedicamento(id: String) {

        repository.obtenerMedicamentoPorId(
            id = id,
            onSuccess = { med ->

                if (med == null) {
                    Toast.makeText(requireContext(), "No encontrado", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                    return@obtenerMedicamentoPorId
                }

                medicamentoActual = med

                // Rellenar campos
                etId.setText(med.id)
                etNombre.setText(med.nombre)
                etPrincipioActivo.setText(med.principioActivo)
                etFrecuencia.setText(med.dosisRecomendada)
                etParaQueSirve.setText(med.paraQueSirve)
                etContraindicaciones.setText(med.contraindicaciones)
                etEfectosSecundarios.setText(med.efectosSecundarios)
                etAdvertencias.setText(med.advertencias)
                etReceta.setText(if (med.requiereReceta) "Sí" else "No")
                etLaboratorio.setText(med.laboratorio)
            },
            onError = { e ->
                Toast.makeText(requireContext(), "Error al cargar detalle: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}
