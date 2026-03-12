package edu.itvo.ejercicio4_solid_hotel

enum class RoomType(val displayName:String, val basePrice: Double) {
    SINGLE("Individual", 500.0),
    DOUBLE("Doble", 800.0),
    SUITE("Suite", 1500.0),
    DELUXE("Deluxe", 2500.0)
}