package edu.itvo.ejercicio3_solid

interface ValidationRule {
    fun validate(items: List<OrderItemDraft>): ValidationResult
}