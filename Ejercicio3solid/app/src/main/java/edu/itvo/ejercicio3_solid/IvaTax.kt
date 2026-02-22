package edu.itvo.ejercicio3_solid

class IvaTax : Tax{
    override fun appliesTo(item: OrderItemDraft): Boolean =
        item.taxable

    override fun calculate(subtotal: Double): Double =
        subtotal * 0.16
}