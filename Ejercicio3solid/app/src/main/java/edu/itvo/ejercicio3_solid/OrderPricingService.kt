package edu.itvo.ejercicio3_solid

class OrderPricingService (private val tax: Tax): calculatePrices{
    override fun calculateSubtotal(items: List<OrderItemDraft>): Double {
        return items.sumOf { it.unitPrice * it.quantity }
    }

    override fun calculateTax(subtotal: Double): Double {
        return tax.calculate(subtotal)
    }

    override fun calculateTotal(subtotal: Double, tax: Double): Double {
        return subtotal+tax
    }
}