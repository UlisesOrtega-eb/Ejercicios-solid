package edu.itvo.ejercicio4_solid_hotel

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
)