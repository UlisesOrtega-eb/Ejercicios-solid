package edu.itvo.ejercicio4_solid_hotel

class ReservationRepositoryInMemory(
    private val hotel: Hotel
) : ReservationRepository {

    override fun save(reservation: Reservation) {
        hotel.reservation.add(reservation)
        reservation.guest.addReservation(reservation)
    }

    override fun findById(id: String): Reservation? {
        return hotel.reservation.find { it.id == id }
    }

    override fun findByGuest(guestId: Int): List<Reservation> {
        return hotel.reservation.filter { it.guest.id == guestId }
    }

    override fun findByRoom(roomNumber: String): List<Reservation> {
        return hotel.reservation.filter { it.room.numberRoom == roomNumber }
    }

    override fun findActiveReservations(): List<Reservation> {
        return hotel.reservation.filter {
            it.status == ReservationStatus.CONFIRMED || it.status == ReservationStatus.PENDING
        }
    }

    override fun update(reservation: Reservation) {
        val index = hotel.reservation.indexOfFirst { it.id == reservation.id }
        if (index != -1) {
            hotel.reservation[index] = reservation
        }
    }
}