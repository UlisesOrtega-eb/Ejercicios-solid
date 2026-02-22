package edu.itvo.ejercicio3_solid

class PositiveQuantitiesRule: ValidationRule {
    override fun validate(items: List<OrderItemDraft>): ValidationResult {
        val invalidItems = items.filter { it.quantity <=0 }
        return if (invalidItems.isNotEmpty()){
            ValidationResult(false, listOf("Todas las cantidades deben ser mayor a 0"))
        }else{
            ValidationResult(true)
        }
    }
}