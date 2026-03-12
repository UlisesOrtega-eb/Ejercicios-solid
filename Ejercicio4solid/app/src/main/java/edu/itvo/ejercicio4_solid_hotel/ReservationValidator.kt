package edu.itvo.ejercicio4_solid_hotel

import javax.xml.validation.Validator

interface ReservationValidator {
    fun validate(room: Room,guest: Guest,dateRange: DateRange): ValidationResult
}