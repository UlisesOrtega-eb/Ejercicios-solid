package edu.itvo.ejercicio4_solid_hotel

class RoomInventoryService(
    private val hotel: Hotel
) : RoomInventoryManager {

    override fun markAsOccupied(room: Room) {
        hotel.rooms.find { it.numberRoom == room.numberRoom }?.availability = false
    }

    override fun markAsAvailable(room: Room) {
        hotel.rooms.find { it.numberRoom == room.numberRoom }?.availability = true
    }
}