package com.fabigroken.proyectomedscan.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// Servicio que manda el texto del OCR a la IA y devuelve la respuesta
class OpenAIService {

    // Cliente HTTP para hacer la petición
    private val client = OkHttpClient()

    // API Key de Groq
    private val apiKey = "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"

    // Función que envía el texto a la IA y recibe la respuesta
    fun analizarTexto(texto: String, callback: (String) -> Unit) {

        // Prompt que le dice a la IA cómo debe responder
        val prompt = """
        Tengo el nombre de un medicamento. 
        Si el texto recibido es solo un número, interprétalo como un código de barras EAN/UPC.
        Completa la información usando tu conocimiento médico general y la normativa de España.
        
        REGLAS IMPORTANTES:
        - NO uses información del envase como: "40 comprimidos", "vía oral", etc.
        - Si el OCR trae cantidades del envase, ignóralas.
        - La posología debe ser real, no lo que diga la caja.
        - No cambies los títulos ni agregues texto extra.
        - Para "Requiere receta", usa la normativa española.
        - NUNCA uses “No disponible”, “No aplicable” o campos vacíos. Si falta información, usa valores típicos o aproximados basados en tu conocimiento médico.
        - No expliques tus decisiones ni agregues comentarios entre paréntesis.
        
        FORMATO EXACTO:
        
        Nombre:
        Concentración:
        Frecuencia y cantidad:
        Para qué sirve:
        Personas que no deberían tomarlo:
        Principio activo:
        Descripción general:
        Contraindicaciones:
        Efectos secundarios:
        Advertencias:
        Requiere receta:
        Laboratorio:
        
        Texto OCR:
        $texto
        """.trimIndent()

        // Construcción del JSON para la API de Groq
        val json = JSONObject()
        json.put("model", "llama-3.1-8b-instant")

        val messages = org.json.JSONArray()
        val userMsg = JSONObject()

        // Mensaje que enviamos a la IA
        userMsg.put("role", "user")
        userMsg.put("content", prompt)
        messages.put(userMsg)

        json.put("messages", messages)

        // Cuerpo de la petición
        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        // Petición HTTP a Groq
        val request = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        // Ejecutar la petición
        client.newCall(request).enqueue(object : Callback {

            // Error de red
            override fun onFailure(call: Call, e: IOException) {
                callback("Error IA red: ${e.message}")
            }

            // Respuesta de la IA
            override fun onResponse(call: Call, response: Response) {

                val responseBody = response.body?.string()

                // Si la API falla
                if (!response.isSuccessful || responseBody == null) {
                    callback("Error IA HTTP ${response.code}: $responseBody")
                    return
                }

                try {
                    // Parseamos la respuesta JSON
                    val jsonResp = JSONObject(responseBody)

                    // Extraemos el texto generado por la IA
                    val output = jsonResp
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim()

                    // Devolvemos el resultado
                    callback(output)

                } catch (e: Exception) {
                    callback("Error procesando respuesta: ${e.message}\n$responseBody")
                }
            }
        })
    }
}
