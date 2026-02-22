package edu.itvo.ejercicio4_solid_hotel

class ReservationValidatorComposite(
    private val rules: List<ValidationRule>
) : ReservationValidator {

    override fun validate(room: Room, guest: Guest, dateRange: DateRange): ValidationResult {
        val errors = mutableListOf<String>()

        rules.forEach { rule ->
            val result = rule.validate(room, guest, dateRange)
            if (!result.isValid) {
                errors.addAll(result.errors)
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult(true)
        } else {
            ValidationResult(false, errors)
        }
    }
}