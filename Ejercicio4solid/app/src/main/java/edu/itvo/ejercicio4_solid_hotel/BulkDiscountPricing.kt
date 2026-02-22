package edu.itvo.ejercicio4_solid_hotel

class BulkDiscountPricing(
    private val minNights: Long = 7,
    private val discountPercent: Double = 0.15
) : PricingStrategy {
    override fun calculate(basePrice: Double, nights: Long): Double {
        val total = basePrice * nights
        return if (nights >= minNights) {
            total * (1 - discountPercent)
        } else {
            total
        }
    }
}