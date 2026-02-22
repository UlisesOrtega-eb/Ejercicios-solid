package edu.itvo.ejercicio4_solid_hotel

interface ReservationCreator {
    fun createReservation(
        room: Room,
        guest: Guest,
        dateRange: DateRange
    ): ReservationResult
}