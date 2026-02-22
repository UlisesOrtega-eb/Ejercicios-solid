package edu.itvo.ejercicio3_solid

class StockAvailabilityRule(private val inventory: Inventory): ValidationRule {
    override fun validate(items: List<OrderItemDraft>): ValidationResult {
        return if (!inventory.checkAvailability(items)){
            ValidationResult(false,listOf("Stock insuficiente para algunos productos"))
        }else{
            ValidationResult(true)
        }
    }
}