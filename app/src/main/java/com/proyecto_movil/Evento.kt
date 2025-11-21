package com.proyecto_movil

data class Evento(
    var fechaEpochDay: Long = 0,
    var titulo: String = "",
    var detalle: String = "",
    var hora: String = ""
) {
    constructor() : this(0, "", "", "")
}