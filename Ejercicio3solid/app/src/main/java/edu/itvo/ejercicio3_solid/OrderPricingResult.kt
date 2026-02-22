package edu.itvo.ejercicio3_solid

data class OrderPricingResult(
    val items: List<OrderItem>,
    val subtotal: Double,
    val discountAmount: Double,
    val taxAmount: Double,
    val total: Double
)