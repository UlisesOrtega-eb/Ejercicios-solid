package edu.itvo.ejercicio4_solid_hotel

data class ReservationResult(
    val success: Boolean,
    val reservation: Reservation? = null,
    val message: String = ""
)