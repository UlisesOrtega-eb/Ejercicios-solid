package edu.itvo.ejercicio3_solid

class NonEmptyCartRule : ValidationRule{
    override fun validate(items: List<OrderItemDraft>): ValidationResult {
        return if (items.isEmpty()){
            ValidationResult(false, listOf("La lista de productos está vacía"))
        }else{
            ValidationResult(true)
        }
    }
}