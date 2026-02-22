package edu.itvo.ejercicio4_solid_hotel

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

class DateRangeValidation : ValidationRule {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun validate(room : Room, guest: Guest, dateRange: DateRange): ValidationResult {
        val today = LocalDate.now()

        return when {
            dateRange.checkIn.isBefore(today) -> {
                ValidationResult(false, listOf("La fecha de entrada no puede ser anterior a hoy"))
            }
            dateRange.checkOut.isBefore(dateRange.checkIn.plusDays(1)) -> {
                ValidationResult(false, listOf("Mínimo 1 noche de estancia"))
            }
            else -> ValidationResult(true)
        }
    }
}