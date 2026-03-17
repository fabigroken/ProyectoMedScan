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

// Librerías de ML Kit para reconocimiento de texto
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
>>>>>>> 637d3a3 (OCR básico y preparación de MainActivity)

class MainActivity : AppCompatActivity() {

    // Variable donde se mostrará el texto detectado
    private lateinit var textoResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
<<<<<<< HEAD
        setContentView(R.layout.activity_main)
    }
}
=======

        // Se carga el diseño de la interfaz
        setContentView(R.layout.activity_main)

        // Se obtiene el botón desde el XML
        val boton = findViewById<Button>(R.id.botonEscanear)

        // Se obtiene el TextView donde se mostrará el resultado
        textoResultado = findViewById(R.id.textoResultado)

        // Evento del botón: al pulsar se abre la cámara
        boton.setOnClickListener {
            abrirCamara()
        }
    }

    // Launcher que permite abrir la cámara y recibir el resultado (imagen)
    private val launcherCamara = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        // Se obtiene la imagen capturada como Bitmap
        val imagen = result.data?.extras?.get("data") as Bitmap

        // Se llama al método que procesa la imagen
        reconocerTexto(imagen)
    }

    // Método para abrir la cámara del dispositivo
    private fun abrirCamara() {

        // Intent que lanza la aplicación de cámara
        val intent = android.content.Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Se ejecuta la cámara
        launcherCamara.launch(intent)
    }

    // Método que utiliza ML Kit para reconocer texto en la imagen
    private fun reconocerTexto(bitmap: Bitmap) {

        // Se convierte la imagen a un formato que ML Kit pueda procesar
        val image = InputImage.fromBitmap(bitmap, 0)

        // Se crea el reconocedor de texto en latín (idiomas como español)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // Se procesa la imagen
        recognizer.process(image)

            // Si funciona correctamente
            .addOnSuccessListener { visionText ->

                // Se muestra el texto detectado en pantalla
                textoResultado.text = visionText.text
            }

            // Si ocurre un error
            .addOnFailureListener {

                // Se muestra mensaje de error
                textoResultado.text = "Error al reconocer texto"
            }
    }
}
>>>>>>> 637d3a3 (OCR básico y preparación de MainActivity)
