package com.fabigroken.proyectomedscan.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Define cómo se hacen las peticiones a la API
interface ApiService {

    // Endpoint para buscar medicamento
    @GET("drug/label.json")
    fun buscarMedicamento(
        @Query("search") nombre: String
    ): Call<Any>
}