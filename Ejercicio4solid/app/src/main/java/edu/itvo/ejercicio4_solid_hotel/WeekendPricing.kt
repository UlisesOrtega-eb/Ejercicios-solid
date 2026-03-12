package edu.itvo.ejercicio4_solid_hotel

class WeekendPricing(private val weekendMultiplayer: Double=1.2) : PricingStrategy{
    override fun calculate(basePrice: Double, nights: Long): Double {
        return basePrice * nights * weekendMultiplayer
    }
}