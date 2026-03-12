package edu.itvo.ejercicio3_solid

import java.time.LocalDateTime

data class OrderItem(
    val productCode: String,
    val productName: String,
    val category: String,
    val unitPrice: Double,
    val quantity: Int,

    val subtotal: Double,
    val discountAmount: Double,
    val taxAmount: Double,
    val total: Double,

    val createdAt: LocalDateTime
)