package com.fabigroken.proyectomedscan

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class EscaneoFragment : Fragment(R.layout.fragment_escaneo) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnIrResultado).setOnClickListener {
            findNavController().navigate(R.id.action_escaneoFragment_to_resultadoFragment)
        }

        view.findViewById<Button>(R.id.btnIrHistorial).setOnClickListener {
            findNavController().navigate(R.id.action_escaneoFragment_to_historialFragment)
        }
    }
}
