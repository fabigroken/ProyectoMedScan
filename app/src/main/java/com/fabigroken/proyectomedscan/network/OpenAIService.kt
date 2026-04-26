package com.fabigroken.proyectomedscan.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// Servicio que se encarga de hablar con la IA (Gemini)
// Recibe el texto del OCR y devuelve la información resumida del medicamento
class OpenAIService {

    // Cliente HTTP para enviar la petición a la IA
    private val client = OkHttpClient()

    // API Key de Google Gemini
    private val apiKey = "AIzaSyD3SAgN7-VIgTkVRBnESbjnSb1d__p2ffo"

    // Función que analiza el texto del medicamento usando Gemini
    fun analizarTexto(texto: String, callback: (String) -> Unit) {

        // Prompt que le enviamos a la IA para que responda corto y claro
        val prompt = """
            Tengo el nombre de un medicamento. 
            Quiero que completes la información REAL aunque no esté en el texto OCR.

            IMPORTANTE:
            - Responde en formato corto.
            - No des explicaciones largas.
            - No inventes datos si no existen.
            - Si falta algo, pon "No disponible", pero igual buscalo por todas las webs para saber que hace ya que si o si necesitamos una respuesta.
            - Tambien en lo de no deberian tomarlo se un poco mas normal en tu respuesta no tanto tecnisismo en las palabras
            - Para que sirve pon textos que se entiendan igual no uses muchos tecnisismos si es para una ulcera ulcera pero no digas que es para atacar una bacteria en concreto o asi
            
            Texto OCR:
            $texto

            Devuelve SOLO esto:
            Nombre:
            Concentración:
            Frecuencia y cantidad:
            Para qué sirve:
            Personas que no deberian tomarlo :
        """.trimIndent()

        // Aquí empezamos a construir el JSON que pide Gemini
        val json = JSONObject()
        val contents = org.json.JSONArray()
        val contentObj = JSONObject()
        val parts = org.json.JSONArray()
        val textPart = JSONObject()

        // Metemos el prompt dentro del JSON
        textPart.put("text", prompt)
        parts.put(textPart)

        // Indicamos que este mensaje lo envía el usuario
        contentObj.put("role", "user")
        contentObj.put("parts", parts)

        // Añadimos todo al contenido principal
        contents.put(contentObj)
        json.put("contents", contents)

        // Convertimos el JSON en cuerpo de la petición
        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        // URL del modelo de Gemini que estamos usando
        val url =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        // Construimos la petición HTTP
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        // Enviamos la petición a la IA
        client.newCall(request).enqueue(object : Callback {

            // Si falla la conexión o no hay internet
            override fun onFailure(call: Call, e: IOException) {
                callback("Error IA red: ${e.message}")
            }

            // Si la IA responde
            override fun onResponse(call: Call, response: Response) {

                val responseBody = response.body?.string()

                // Si la IA devuelve error, lo mostramos
                if (!response.isSuccessful || responseBody == null) {
                    callback("Error IA HTTP ${response.code}: $responseBody")
                    return
                }

                try {
                    // Convertimos la respuesta en JSON
                    val jsonResp = JSONObject(responseBody)

                    // Aquí está el texto generado por Gemini
                    val output = jsonResp
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                    // Devolvemos el resultado a la Activity
                    callback(output)

                } catch (e: Exception) {
                    // Si algo falla al leer el JSON
                    callback("Error procesando respuesta: ${e.message}\n$responseBody")
                }
            }
        })
    }
}
