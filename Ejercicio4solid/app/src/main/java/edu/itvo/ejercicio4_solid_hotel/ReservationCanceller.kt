package edu.itvo.ejercicio4_solid_hotel

interface ReservationCanceller {
    fun cancelReservation(reservationId: String): CancellationResult
}