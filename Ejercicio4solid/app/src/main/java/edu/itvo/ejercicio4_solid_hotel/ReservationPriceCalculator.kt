package edu.itvo.ejercicio4_solid_hotel

interface ReservationPriceCalculator {
    fun calculateTotal(room: Room,dateRange: DateRange): Double
}