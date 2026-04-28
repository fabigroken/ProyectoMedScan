package com.fabigroken.proyectomedscan

data class Medicamento(
    val id: String = "",
    val nombre: String = "",
    val principioActivo: String = "",
    val descripcionGeneral: String = "",
    val paraQueSirve: String = "",
    val contraindicaciones: String = "",
    val efectosSecundarios: String = "",
    val dosisRecomendada: String = "",
    val advertencias: String = "",
    val requiereReceta: Boolean = false,
    val laboratorio: String = "",
    val fechaEscaneo: Long = 0L,
    val fuenteInformacion: String = ""
)
