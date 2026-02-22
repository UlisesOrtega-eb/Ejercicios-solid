package edu.itvo.ejercicio4_solid_hotel

class SeasonalPricing(private val seasonMultiplier: Double = 1.5) : PricingStrategy {
    override fun calculate(basePrice: Double, nights: Long): Double {
        return basePrice * nights * seasonMultiplier
    }
}