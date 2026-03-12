package edu.itvo.ejercicio3_solid

import java.time.LocalDateTime

class OrderItemFactory(private val tax: Tax) {
    fun createOrderItems(drafts: List<OrderItemDraft>): List<OrderItem> {
        return drafts.map { draft ->
            val subtotal = draft.unitPrice * draft.quantity
            val discountAmount = 0.0 // Sin descuentos por ahora

            val taxAmount = if (tax.appliesTo(draft)) {
                tax.calculate(subtotal)
            } else {
                0.0
            }

            val total = subtotal + taxAmount

            OrderItem(
                productCode = draft.productCode,
                productName = draft.productName,
                category = draft.category,
                unitPrice = draft.unitPrice,
                quantity = draft.quantity,
                subtotal = subtotal,
                discountAmount = discountAmount,
                taxAmount = taxAmount,
                total = total,
                createdAt = LocalDateTime.now()
            )
        }
    }
}