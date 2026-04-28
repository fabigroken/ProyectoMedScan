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

    private lateinit var etId: EditText
    private lateinit var etNombre: EditText
    private lateinit var etPrincipioActivo: EditText
    private lateinit var etDosis: EditText
    private lateinit var etAdvertencias: EditText
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etId = view.findViewById(R.id.etId)
        etNombre = view.findViewById(R.id.etNombre)
        etPrincipioActivo = view.findViewById(R.id.etPrincipioActivo)
        etDosis = view.findViewById(R.id.etDosis)
        etAdvertencias = view.findViewById(R.id.etAdvertencias)
        btnActualizar = view.findViewById(R.id.btnActualizar)
        btnEliminar = view.findViewById(R.id.btnEliminar)

        val id = arguments?.getString("medicamentoId")
        if (id.isNullOrBlank()) {
            Toast.makeText(requireContext(), "ID no válido", Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
            return
        }

        cargarMedicamento(id)

        btnActualizar.setOnClickListener {
            val original = medicamentoActual ?: return@setOnClickListener
            val actualizado = original.copy(
                nombre = etNombre.text.toString().trim(),
                principioActivo = etPrincipioActivo.text.toString().trim(),
                dosisRecomendada = etDosis.text.toString().trim(),
                advertencias = etAdvertencias.text.toString().trim()
            )

            repository.actualizarMedicamento(
                medicamento = actualizado,
                onSuccess = {
                    medicamentoActual = actualizado
                    Toast.makeText(requireContext(), "Medicamento actualizado", Toast.LENGTH_SHORT).show()
                },
                onError = { e ->
                    Toast.makeText(requireContext(), "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }

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
                etId.setText(med.id)
                etNombre.setText(med.nombre)
                etPrincipioActivo.setText(med.principioActivo)
                etDosis.setText(med.dosisRecomendada)
                etAdvertencias.setText(med.advertencias)
            },
            onError = { e ->
                Toast.makeText(requireContext(), "Error al cargar detalle: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}
