package com.fabigroken.proyectomedscan

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ResultadoFragment : Fragment(R.layout.fragment_resultado) {

    private val repository = MedicamentoRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnGuardarDemo).setOnClickListener {
            val med = Medicamento(
                id = "med_" + System.currentTimeMillis(),
                nombre = "Ibuprofeno 600",
                principioActivo = "Ibuprofeno",
                descripcionGeneral = "Antiinflamatorio no esteroideo",
                paraQueSirve = "Dolor e inflamacion",
                contraindicaciones = "Ulcera gastrica",
                efectosSecundarios = "Nauseas",
                dosisRecomendada = "1 comprimido cada 8 horas",
                advertencias = "Tomar con comida",
                requiereReceta = true,
                laboratorio = "Laboratorio X",
                fechaEscaneo = System.currentTimeMillis(),
                fuenteInformacion = "API MedScan"
            )

            repository.guardarMedicamento(
                medicamento = med,
                onSuccess = {
                    Toast.makeText(requireContext(), "Guardado en Firestore", Toast.LENGTH_SHORT).show()
                },
                onError = { e ->
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }

        view.findViewById<Button>(R.id.btnIrHistorialDesdeResultado).setOnClickListener {
            findNavController().navigate(R.id.action_resultadoFragment_to_historialFragment)
        }
    }
}
