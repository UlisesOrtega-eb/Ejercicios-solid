package edu.itvo.ejercicio3_solid

interface Inventory {
    fun checkAvailability(items: List<OrderItemDraft>): Boolean
    fun reduceStock(items: List<OrderItemDraft>)
    fun restore(items: List<OrderItemDraft>)
}