package com.fabigroken.proyectomedscan

// Importaciones necesarias para trabajar con cámara, interfaz y OCR
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity
=======
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider

// Librerías de ML Kit para reconocimiento de texto (OCR)
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
>>>>>>> 637d3a3 (OCR básico y preparación de MainActivity)

// Librerías para escaneo de códigos de barras
import com.google.mlkit.vision.barcode.BarcodeScanning
import java.io.File

class MainActivity : AppCompatActivity() {

    // Aquí se mostrará todo lo que saque el OCR o la IA
    private lateinit var textoResultado: TextView

    // Para saber si el usuario quiere OCR o código de barras
    private var modoEscaneo = "OCR"

    // NECESARIO PARA QUE NO CRASHEE
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
<<<<<<< HEAD
        setContentView(R.layout.activity_main)
    }
}
=======

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            e.printStackTrace()
        }

        // Comprobamos si el usuario ha dado permiso para usar la cámara
        // Si no lo ha dado, se pide al abrir la app
        if (checkSelfPermission(android.Manifest.permission.CAMERA)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
        }

        // Cargamos la interfaz
        setContentView(R.layout.activity_main)

        // Botones del layout
        val botonOCR = findViewById<Button>(R.id.botonOCR)
        val botonBarcode = findViewById<Button>(R.id.botonBarcode)

        // Donde se mostrará el resultado final
        textoResultado = findViewById(R.id.textoResultado)

        // Botón para OCR normal
        botonOCR.setOnClickListener {
            modoEscaneo = "OCR"
            abrirCamara()
        }

        // Botón para leer códigos de barras
        botonBarcode.setOnClickListener {
            modoEscaneo = "BARCODE"
            abrirCamara()
        }
    }

    // Abre la cámara y espera la foto
    private val launcherCamara = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        // Si la foto se tomó bien
        if (result.resultCode == RESULT_OK) {

            // CARGAMOS LA FOTO REAL (NO MINIATURA)
            val imagen = BitmapFactory.decodeFile(photoFile.absolutePath)

            if (imagen != null) {

                // Dependiendo del modo, hacemos una cosa u otra
                if (modoEscaneo == "BARCODE") {
                    escanearCodigoBarras(imagen)
                } else {
                    reconocerTexto(imagen)
                }

            } else {
                // Si por lo que sea no llega la imagen
                textoResultado.text = "Error: no se pudo obtener la imagen"
            }

        } else {
            // Si el usuario cancela la foto
            textoResultado.text = "Captura cancelada"
        }
    }

    // Método que abre la cámara del dispositivo
    private fun abrirCamara() {

        // CREAMOS ARCHIVO TEMPORAL PARA LA FOTO REAL
        photoFile = File.createTempFile("foto_", ".jpg", cacheDir)

        photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            photoFile
        )

        val intent = android.content.Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

        launcherCamara.launch(intent)
    }

    // OCR: extrae texto de la imagen
    private fun reconocerTexto(bitmap: Bitmap) {

        // Convertimos el Bitmap a formato que ML Kit entiende
        val image = InputImage.fromBitmap(bitmap, 0)

        // Creamos el reconocedor de texto
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // Procesamos la imagen
        recognizer.process(image)
            .addOnSuccessListener { visionText ->

                // Si no hay texto, avisamos
                if (visionText.text.isEmpty()) {
                    textoResultado.text = "No se detectó texto"
                } else {

                    // Texto tal cual lo detecta ML Kit
                    val textoDetectado = visionText.text

                    // Limpieza rápida para evitar saltos raros
                    val textoLimpio = limpiarTextoOCR(textoDetectado)

                    // Indicamos que estamos enviando todo a la IA
                    textoResultado.text = "Analizando texto con IA..."

                    // Mandamos el texto a OpenAI
                    analizarConIA(textoLimpio)
                }
            }
            .addOnFailureListener { e ->
                // Si falla el OCR
                textoResultado.text = "Error OCR: ${e.message}"
            }
    }

    // Limpieza básica del texto del OCR
    private fun limpiarTextoOCR(texto: String): String {
        return texto
            .replace("\n", " ")
            .trim()
    }

    // Escaneo de códigos de barras
    private fun escanearCodigoBarras(bitmap: Bitmap) {

        val image = InputImage.fromBitmap(bitmap, 0)

        // Cliente de ML Kit para leer códigos
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->

                // Si detecta al menos uno
                if (barcodes.isNotEmpty()) {

                    // Cogemos el primer código detectado
                    val codigo = barcodes[0].rawValue ?: ""

                    // Mostramos el código detectado
                    textoResultado.text = "Código detectado: $codigo\nBuscando también por texto..."

                    // Hacemos OCR igualmente para sacar info del envase
                    reconocerTexto(bitmap)

                } else {
                    // Si no detecta código, tiramos de OCR directamente
                    reconocerTexto(bitmap)
                }
            }
            .addOnFailureListener {
                textoResultado.text = "Error escaneando código"
            }
    }
<<<<<<< HEAD
}
>>>>>>> 637d3a3 (OCR básico y preparación de MainActivity)
=======

    // Envia el texto completo del OCR a la IA para que lo entienda
    private fun analizarConIA(texto: String) {

        val ia = com.fabigroken.proyectomedscan.network.OpenAIService()

        ia.analizarTexto(texto) { resultado ->

            runOnUiThread {
                textoResultado.text = resultado
            }
        }
    }

}
>>>>>>> c313b67 (IA funcional de medicamentos scanner foto y barras)
