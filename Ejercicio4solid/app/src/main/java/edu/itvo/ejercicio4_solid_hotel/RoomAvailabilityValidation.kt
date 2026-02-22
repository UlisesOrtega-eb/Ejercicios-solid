package edu.itvo.ejercicio4_solid_hotel

class RoomAvailabilityValidation(
    private val availabilityChecker: RoomAvailabilityChecker
) : ValidationRule {
    override fun validate(room: Room, guest: Guest, dateRange: DateRange): ValidationResult {
        return if (!availabilityChecker.isAvailable(room, dateRange)) {
            ValidationResult(false, listOf("La habitación no está disponible en las fechas solicitadas"))
        } else {
            ValidationResult(true)
        }
    }
}