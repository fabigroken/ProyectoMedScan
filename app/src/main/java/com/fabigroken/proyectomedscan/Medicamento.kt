package com.fabigroken.proyectomedscan
data class Medicamento(

    // ID único del medicamento
    val id: String = "",

    // Nombre comercial del medicamento (ej: "Ibuprofeno 600")
    val nombre: String = "",

    // Principio activo (ej: "Ibuprofeno")
    val principioActivo: String = "",

    // Descripción general del medicamento
    val descripcionGeneral: String = "",

    // Para qué sirve
    val paraQueSirve: String = "",

    // Contraindicaciones o alergias
    val contraindicaciones: String = "",

    // Efectos secundarios
    val efectosSecundarios: String = "",

    // Dosis recomendada
    val dosisRecomendada: String = "",

    // Advertencias generales
    val advertencias: String = "",

    // Si requiere receta o no
    val requiereReceta: Boolean = false,

    // Laboratorio fabricante
    val laboratorio: String = "",

    // Fecha en la que se escaneó
    val fechaEscaneo: Long = 0L,

    // Fuente de la información
    val fuenteInformacion: String = ""
)
