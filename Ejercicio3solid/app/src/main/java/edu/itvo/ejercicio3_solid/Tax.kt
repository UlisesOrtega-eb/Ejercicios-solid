package edu.itvo.ejercicio3_solid

interface Tax {
    fun appliesTo(item: OrderItemDraft): Boolean
    fun calculate(subtotal: Double): Double
}