package edu.itvo.ejercicio4_solid_hotel

data class Room(
    val numberRoom: String,
    val roomType: RoomType,
    val priceNight: Double,
    var availability: Boolean = true
)