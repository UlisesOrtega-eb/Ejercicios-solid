package edu.itvo.ejercicio4_solid_hotel

enum class ReservationStatus(val canBeCancelled: Boolean) {
    PENDING(true),
    CONFIRMED(true),
    COMPLETED(false),
    CANCELLED(false);
}