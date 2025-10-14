package com.proyecto_movil

data class Comunicado(
    val emisorAvatarRes: Int,
    val emisorNombre: String,
    val hora: String,
    val titulo: String,
    val preview: String
)