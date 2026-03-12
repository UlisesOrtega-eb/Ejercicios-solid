package edu.itvo.ejercicio4_solid_hotel

data class Guest(
    val guestId: String, // Changed from 'id: Int' to 'guestId: String'
    val name: String,
    val dni: String,
    private val historyBooking: MutableList<Reservation> = mutableListOf()
)
{
    fun addReservation(reservation: Reservation){
        historyBooking.add(reservation)
    }

    fun getReservationHistory():List<Reservation> =
        historyBooking.toList()
}