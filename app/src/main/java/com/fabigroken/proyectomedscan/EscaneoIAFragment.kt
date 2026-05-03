package com.fabigroken.proyectomedscan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.fabigroken.proyectomedscan.network.OpenAIService
import java.io.File
import android.app.Activity
import android.content.pm.PackageManager

class EscaneoIAFragment : Fragment(R.layout.fragment_escaneo_ia) {

    // Texto donde muestro el resultado del OCR o IA
    private lateinit var textoResultado: TextView

    // Para saber si estoy escaneando OCR o código de barras
    private var modoEscaneo = "OCR"

    // Archivo temporal donde guardo la foto
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    // Repositorio para guardar en Firestore
    private val repository = MedicamentoRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón para volver atrás
        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener { findNavController().popBackStack() }

        // Botones del layout
        val botonOCR = view.findViewById<Button>(R.id.botonOCR)
        val botonBarcode = view.findViewById<Button>(R.id.botonBarcode)
        textoResultado = view.findViewById(R.id.textoResultado)

        // Cuando el usuario quiere escanear texto
        botonOCR.setOnClickListener {
            modoEscaneo = "OCR"

            if (tienePermisoCamara()) {
                abrirCamara()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
            }
        }

        // Cuando el usuario quiere escanear código de barras
        botonBarcode.setOnClickListener {
            modoEscaneo = "BARCODE"

            if (tienePermisoCamara()) {
                abrirCamara()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
            }
        }
    }

    // Lanzador de la cámara
    private val launcherCamara = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            val imagen = BitmapFactory.decodeFile(photoFile.absolutePath)

            if (imagen != null) {
                if (modoEscaneo == "BARCODE") {
                    escanearCodigoBarras(imagen)
                } else {
                    reconocerTexto(imagen)
                }
            } else {
                textoResultado.text = "Error: no se pudo obtener la imagen"
            }

        } else {
            textoResultado.text = "Captura cancelada"
        }
    }

    // Abre la cámara usando FileProvider
    private fun abrirCamara() {

        photoFile = File.createTempFile("foto_", ".jpg", requireContext().cacheDir)

        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )

        val intent = android.content.Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

        intent.addFlags(android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)

        launcherCamara.launch(intent)
    }

    // Hace OCR sobre la imagen
    private fun reconocerTexto(bitmap: Bitmap) {

        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->

                if (visionText.text.isEmpty()) {
                    textoResultado.text = "No se detectó texto"
                } else {
                    val textoDetectado = visionText.text
                    val textoLimpio = limpiarTextoOCR(textoDetectado)

                    textoResultado.text = "Analizando texto con IA..."
                    analizarConIA(textoLimpio)
                }
            }
            .addOnFailureListener { e ->
                textoResultado.text = "Error OCR: ${e.message}"
            }
    }

    // Limpia saltos de línea del OCR
    private fun limpiarTextoOCR(texto: String): String {
        return texto.replace("\n", " ").trim()
    }

    // Escaneo de código de barras
    private fun escanearCodigoBarras(bitmap: Bitmap) {

        textoResultado.text = "Escaneando código de barras..."

        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->

                if (barcodes.isNotEmpty()) {
                    val codigo = barcodes[0].rawValue ?: ""
                    textoResultado.text = "Código detectado: $codigo\nAnalizando código con IA..."
                    analizarConIA(codigo)

                } else {
                    reconocerTexto(bitmap)
                }
            }
            .addOnFailureListener {
                textoResultado.text = "Error escaneando código"
            }
    }

    // Llama a la IA para analizar el texto
    private fun analizarConIA(texto: String) {

        val ia = OpenAIService()

        ia.analizarTexto(texto) { resultadoIA ->

            requireActivity().runOnUiThread {

                // Parseo el texto de la IA a un mapa clave-valor
                val mapa = parsearResultadoIA(resultadoIA)

                // Si la IA no devuelve algo usable (por ejemplo, sin Nombre), NO guardamos
                if (!mapa.containsKey("Nombre") || mapa["Nombre"].isNullOrBlank()) {
                    textoResultado.text = "Foto no entendible. Scanea de nuevo."
                    return@runOnUiThread
                }


                // Creo el objeto Medicamento con los datos de la IA
                val medicamento = Medicamento(
                    id = "med_${System.currentTimeMillis()}",
                    nombre = mapa["Nombre"] ?: "",
                    principioActivo = mapa["Principio activo"] ?: "",
                    descripcionGeneral = mapa["Descripción general"] ?: "",
                    paraQueSirve = mapa["Para qué sirve"] ?: "",
                    contraindicaciones = mapa["Contraindicaciones"] ?: "",
                    efectosSecundarios = mapa["Efectos secundarios"] ?: "",
                    dosisRecomendada = mapa["Frecuencia y cantidad"] ?: "",
                    advertencias = mapa["Advertencias"] ?: "",
                    requiereReceta = (mapa["Requiere receta"] ?: "No") == "Sí",
                    laboratorio = mapa["Laboratorio"] ?: "",
                    fechaEscaneo = System.currentTimeMillis(),
                    fuenteInformacion = "OCR + IA"
                )

                // Mostrar esos 5 campos
                val textoVisible = """
                <b>Nombre</b><br>
                ${mapa["Nombre"]}<br><br>
                
                <b>Concentración</b><br>
                ${mapa["Concentración"]}<br><br>
                
                <b>Frecuencia y cantidad</b><br>
                ${mapa["Frecuencia y cantidad"]}<br><br>
                
                <b>Para qué sirve</b><br>
                ${mapa["Para qué sirve"]}<br><br>
                
                <b>Personas que no deberían tomarlo</b><br>
                ${mapa["Personas que no deberían tomarlo"]}
                """.trimIndent()

                textoResultado.text = android.text.Html.fromHtml(textoVisible)


                // Guardado automático SIN mostrar mensajes al usuario
                repository.guardarSinDuplicar(
                    medicamento,
                    onSuccess = { },
                    onDuplicate = { },
                    onError = { }
                )
            }
        }
    }

    // Parser tolerante a errores, espacios, saltos y formatos raros
    private fun parsearResultadoIA(texto: String): Map<String, String> {

        val mapa = mutableMapOf<String, String>()
        val lineas = texto.split("\n")

        var claveActual = ""
        var valorActual = ""

        val regexClave = Regex("^([A-Za-zÁÉÍÓÚáéíóúñÑ ]+):\\s*(.*)$")

        for (linea in lineas) {
            val lineaLimpia = linea.trim()
            val match = regexClave.find(lineaLimpia)

            if (match != null) {
                if (claveActual.isNotEmpty()) {
                    mapa[claveActual] = valorActual.trim()
                }

                claveActual = match.groupValues[1].trim()
                valorActual = match.groupValues[2].trim()

            } else {
                if (claveActual.isNotEmpty()) {
                    valorActual += " $lineaLimpia"
                }
            }
        }

        if (claveActual.isNotEmpty()) {
            mapa[claveActual] = valorActual.trim()
        }

        return mapa
    }

   //Permisos de camara
    private fun tienePermisoCamara(): Boolean {
        return requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            abrirCamara()
        } else {
            textoResultado.text = "Permiso de cámara denegado"
        }
    }
}