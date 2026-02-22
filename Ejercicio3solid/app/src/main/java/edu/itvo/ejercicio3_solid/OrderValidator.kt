package edu.itvo.ejercicio3_solid

class OrderValidator (private val rules: List<ValidationRule>){
    fun validate(items: List<OrderItemDraft>): ValidationResult {
        val errors = mutableListOf<String>()

        rules.forEach { rule ->
            val result = rule.validate(items)
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