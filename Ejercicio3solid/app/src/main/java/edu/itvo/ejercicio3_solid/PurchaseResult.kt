package edu.itvo.ejercicio3_solid

data class PurchaseResult(
    val success: Boolean,
    val order: Order? = null,
    val message: String = ""
)
