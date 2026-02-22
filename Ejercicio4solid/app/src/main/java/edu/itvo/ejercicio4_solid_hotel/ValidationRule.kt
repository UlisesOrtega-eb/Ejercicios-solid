package edu.itvo.ejercicio4_solid_hotel

interface ValidationRule {
    fun validate(room: Room, guest: Guest, dateRange: DateRange): ValidationResult
}