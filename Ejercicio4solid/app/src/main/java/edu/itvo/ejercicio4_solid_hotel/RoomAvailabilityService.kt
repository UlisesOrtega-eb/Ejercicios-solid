package edu.itvo.ejercicio4_solid_hotel

class RoomAvailabilityService (
    private val hotel: Hotel
) : RoomAvailabilityChecker {

    override fun isAvailable(room: Room, dateRange: DateRange): Boolean {
        // Verificar si la habitación está disponible
        if (!room.availability) return false

        // Verificar si hay reservas que se solapan con las fechas solicitadas
        val overlappingReservations = hotel.reservation.filter { reservation ->
            reservation.room.numberRoom == room.numberRoom &&
                    reservation.status != ReservationStatus.CANCELLED &&
                    reservation.dateRange.overlaps(dateRange)
        }

        return overlappingReservations.isEmpty()
    }

    override fun findAvailableRooms(dateRange: DateRange): List<Room> {
        return hotel.rooms.filter { room ->
            isAvailable(room, dateRange)
        }
    }

    override fun findAvailableRoomsByType(type: RoomType, dateRange: DateRange): List<Room> {
        return hotel.rooms.filter { room ->
            room.roomType == type && isAvailable(room, dateRange)
        }
    }
}