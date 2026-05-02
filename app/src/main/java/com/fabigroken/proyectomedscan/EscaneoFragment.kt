package com.fabigroken.proyectomedscan

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

// Pantalla inicial: desde aquí se va al historial o a tu escaneo con IA
class EscaneoFragment : Fragment(R.layout.fragment_escaneo) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ir al historial
        view.findViewById<Button>(R.id.btnIrHistorial).setOnClickListener {
            findNavController().navigate(R.id.action_escaneoFragment_to_historialFragment)
        }

        // Ir a tu pantalla de escaneo con IA
        view.findViewById<Button>(R.id.btnIrEscaneoIA).setOnClickListener {
            findNavController().navigate(R.id.action_escaneoFragment_to_escaneoIAFragment)
        }
    }
}
