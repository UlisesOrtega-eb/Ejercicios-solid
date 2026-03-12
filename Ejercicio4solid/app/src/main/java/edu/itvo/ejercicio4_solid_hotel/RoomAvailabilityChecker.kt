package edu.itvo.ejercicio4_solid_hotel

interface RoomAvailabilityChecker {
    fun isAvailable(room: Room, dateRange: DateRange): Boolean
    fun findAvailableRooms(dateRange: DateRange): List<Room>
    fun findAvailableRoomsByType(type: RoomType, dateRange: DateRange): List<Room>
}