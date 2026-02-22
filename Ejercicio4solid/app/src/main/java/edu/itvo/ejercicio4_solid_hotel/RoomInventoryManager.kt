package edu.itvo.ejercicio4_solid_hotel

interface RoomInventoryManager {
    fun markAsOccupied(room: Room)
    fun markAsAvailable(room: Room)
}