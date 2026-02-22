package edu.itvo.ejercicio4_solid_hotel

import android.os.Build
import androidx.annotation.RequiresApi

class MaximumStayValidation(private val maxNights: Long = 30) : ValidationRule {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun validate(room: Room, guest: Guest, dateRange: DateRange): ValidationResult {
        return if (dateRange.numberOfNights() > maxNights) {
            ValidationResult(false, listOf("La estancia máxima es de $maxNights noches"))
        } else {
            ValidationResult(true)
        }
    }
}