package com.fabigroken.proyectomedscan

// Importaciones necesarias para trabajar con cámara, interfaz y OCR
import android.graphics.Bitmap
import android.os.Bundle
<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity
=======
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

// Librerías de ML Kit para reconocimiento de texto (OCR)
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
>>>>>>> 637d3a3 (OCR básico y preparación de MainActivity)

// Librerías para escaneo de códigos de barras
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

// Importaciones para la conexión con la API (peticiones HTTP)
import com.fabigroken.proyectomedscan.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // Variable donde se mostrará el resultado final en pantalla
    private lateinit var textoResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
<<<<<<< HEAD
        setContentView(R.layout.activity_main)
    }
}
=======

        // Comprobamos si el usuario ha dado permiso para usar la cámara
        // Si no lo ha dado, se solicita al abrir la aplicacion
        if (checkSelfPermission(android.Manifest.permission.CAMERA)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
        }

        // Se carga la interfaz definida en XML
        setContentView(R.layout.activity_main)

        // Se obtienen los botones definidos en el layout
        val botonOCR = findViewById<Button>(R.id.botonOCR)
        val botonBarcode = findViewById<Button>(R.id.botonBarcode)

        // Se inicializa el TextView donde se mostrará el resultado
        textoResultado = findViewById(R.id.textoResultado)

        // Configuración del botón de escaneo OCR (texto)
        botonOCR.setOnClickListener {
            modoEscaneo = "OCR"
            abrirCamara()
        }

        // Configuración del botón de escaneo de código de barras
        botonBarcode.setOnClickListener {
            modoEscaneo = "BARCODE"
            abrirCamara()
        }
    }

    // Launcher para abrir la cámara y recibir el resultado
    private val launcherCamara = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        // Verificamos que la captura fue correcta
        if (result.resultCode == RESULT_OK && result.data != null) {

            val extras = result.data!!.extras

            // Se obtiene la imagen en formato Bitmap
            val imagen = extras?.get("data") as? Bitmap

            if (imagen != null) {

                // Dependiendo del modo seleccionado, usamos OCR o código de barras
                if (modoEscaneo == "BARCODE") {
                    escanearCodigoBarras(imagen)
                } else {
                    reconocerTexto(imagen)
                }

            } else {
                // Control de error si la imagen no se pudo obtener
                textoResultado.text = "Error: no se pudo obtener la imagen (bitmap null)"
            }

        } else {
            // Si el usuario cancela la foto
            textoResultado.text = "Captura cancelada"
        }
    }

    // Método que abre la cámara del dispositivo mediante un Intent
    private fun abrirCamara() {
        val intent = android.content.Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcherCamara.launch(intent)
    }

    // Método principal de OCR que analiza la imagen y extrae texto
    private fun reconocerTexto(bitmap: Bitmap) {

        // Convertimos el Bitmap en un formato que ML Kit pueda procesar
        val image = InputImage.fromBitmap(bitmap, 0)

        // Creamos el reconocedor de texto
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // Procesamos la imagen
        recognizer.process(image)
            .addOnSuccessListener { visionText ->

                // Si no se detecta texto
                if (visionText.text.isEmpty()) {
                    textoResultado.text = "No se detectó texto"
                } else {
                    val textoDetectado = visionText.text

                    // Limpieza básica del texto su formato en si
                    val textoLimpio = limpiarTextoOCR(textoDetectado)

                    // Se envía el texto a la API para obtener información del medicamento
                    buscarInfoMedicamento(textoLimpio)
                }
            }
            .addOnFailureListener { e ->
                // Error en el OCR
                textoResultado.text = "Error OCR: " + e.message
            }
    }

    // Método para limpiar el texto obtenido del OCR
    private fun limpiarTextoOCR(texto: String): String {
        return texto
            .replace("\n", " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

    // Método que realiza la llamada a la API externa usando Retrofit
    private fun buscarInfoMedicamento(texto: String) {

        // Se construye la petición a la API
        val call = RetrofitClient.apiService.buscarMedicamento(texto)
        call.enqueue(object : Callback<Any> {

            // Se ejecuta cuando la API responde correctamente
            override fun onResponse(call: Call<Any>, response: Response<Any>) {

                if (response.isSuccessful) {
                    // Se muestra la respuesta en pantalla
                    textoResultado.text = "Resultado API:\n" + response.body().toString()
                } else {
                    textoResultado.text = "No se encontró información"
                }
            }

            // Se ejecuta si hay fallo de red o error en la petición
            override fun onFailure(call: Call<Any>, t: Throwable) {
                textoResultado.text = "Error de conexión: " + t.message
            }
        })
    }

    // Para indicar qué tipo de escaneo se está utilizando
    private var modoEscaneo = "OCR"

    // Método para detectar códigos de barras en la imagen
    private fun escanearCodigoBarras(bitmap: Bitmap) {

        val image = InputImage.fromBitmap(bitmap, 0)

        // Cliente de ML Kit para escaneo de códigos
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->

                // Si detecta al menos un código
                if (barcodes.isNotEmpty()) {

                    // Se obtiene el valor del primer código detectado
                    val codigo = barcodes[0].rawValue ?: ""

                    // Se muestra el código detectado
                    textoResultado.text = "Código detectado: $codigo\nBuscando también por texto..."

                    // Usamos el OCR como respaldo
                    reconocerTexto(bitmap)

                } else {
                    // Si no hay código, se usa OCR directamente
                    reconocerTexto(bitmap)
                }
            }
            .addOnFailureListener {
                textoResultado.text = "Error escaneando código"
            }
    }
}
>>>>>>> 637d3a3 (OCR básico y preparación de MainActivity)
