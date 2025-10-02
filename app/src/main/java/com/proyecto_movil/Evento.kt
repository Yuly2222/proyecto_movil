package com.proyecto_movil

data class Evento(
    val fechaEpochDay: Long, // Día en epoch
    val titulo: String,
    val detalle: String,
    val hora: String
)