package edu.itvo.ejercicio4_solid_hotel

interface PricingStrategy {
    fun calculate(basePrice: Double, nights: Long): Double
}