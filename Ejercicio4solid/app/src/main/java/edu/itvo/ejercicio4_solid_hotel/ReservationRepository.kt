package edu.itvo.ejercicio4_solid_hotel

interface ReservationRepository {
    fun save(reservation: Reservation)
    fun findById(id: String): Reservation?
    fun findByGuestId(guestId: String): List<Reservation>
    fun findByRoomNumber(roomNumber: String): List<Reservation>
    fun findActiveReservations(): List<Reservation>
    fun update(reservation: Reservation)
}