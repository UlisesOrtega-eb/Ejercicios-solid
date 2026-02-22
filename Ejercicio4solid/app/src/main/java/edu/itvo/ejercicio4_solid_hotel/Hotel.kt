package edu.itvo.ejercicio4_solid_hotel

data class Hotel(
    val name: String,
    val rooms: MutableList<Room> = mutableListOf(),
    val reservation: MutableList<Reservation> = mutableListOf(),
    val guest: MutableList<Guest> = mutableListOf()
)
