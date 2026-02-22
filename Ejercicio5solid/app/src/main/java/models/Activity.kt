package models

data class Activity(
    val nombre: String,
    val ponente: Speaker,
    val horaInicio: Int, // Usaremos formato 24h (ej. 1400 para 2:00 PM)
    val horaFin: Int,
    val cupoMaximo: Int,
    val inscritos: MutableList<Assistant> = mutableListOf()
)
