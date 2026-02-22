package edu.itvo.ejercicio4_solid_hotel

interface ReservationRepository {
    fun save(reservation: Reservation)
    fun findById(id: String): Reservation?
    fun findByGuest(guestId: Int): List<Reservation>
    fun findByRoom(roomNumber: String): List<Reservation>
    fun findActiveReservations(): List<Reservation>
    fun update(reservation: Reservation)
}