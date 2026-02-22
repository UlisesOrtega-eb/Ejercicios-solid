package edu.itvo.ejercicio3_solid

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
)