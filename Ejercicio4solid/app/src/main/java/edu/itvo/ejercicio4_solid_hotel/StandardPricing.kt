package edu.itvo.ejercicio4_solid_hotel

class StandardPricing : PricingStrategy{
    override fun calculate(basePrice: Double, nights: Long): Double {
        return basePrice * nights
    }
}