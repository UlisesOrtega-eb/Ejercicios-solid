package edu.itvo.ejercicio4_solid_hotel

interface ReservationQuery {
    fun findReservation(id: String): Reservation?
    fun getGuestReservations(guestId: Int): List<Reservation>
    fun getRoomReservations(roomNumber: String): List<Reservation>
}