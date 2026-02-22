package edu.itvo.ejercicio3_solid

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.util.UUID

data class Order (
    val id: String,
    val customer: Customer,
    val items: List<OrderItem>,
    val subtotal: Double,
    val discountAmount: Double,
    val taxAmount: Double,
    val total: Double,
    val status: OrderStatus,
    val createdAt: LocalDateTime
) {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun from(
            customer: Customer,
            pricingResult: OrderPricingResult
        ): Order {
            return Order(
                id = UUID.randomUUID().toString(),
                customer = customer,
                items = pricingResult.items,
                subtotal = pricingResult.subtotal,
                discountAmount = pricingResult.discountAmount,
                taxAmount = pricingResult.taxAmount,
                total = pricingResult.total,
                status = OrderStatus.CREATED,
                createdAt = LocalDateTime.now()
            )
        }
    }
}