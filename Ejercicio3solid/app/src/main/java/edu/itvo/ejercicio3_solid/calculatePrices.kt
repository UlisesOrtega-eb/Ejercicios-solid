package edu.itvo.ejercicio3_solid

interface calculatePrices {
    fun calculateSubtotal(items: List<OrderItemDraft>): Double
    fun calculateTax(subtotal: Double): Double
    fun calculateTotal(subtotal: Double,tax: Double): Double
}