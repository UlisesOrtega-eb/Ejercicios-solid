package edu.itvo.ejercicio4_solid_hotel

interface ReservationQuery {
    fun findReservation(id: String): Reservation?

    fun findReservationsByGuestId(guestId: String): List<Reservation>

    fun findReservationsByRoomNumber(roomNumber: String): List<Reservation>
}