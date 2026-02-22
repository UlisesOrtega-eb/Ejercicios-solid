package edu.itvo.ejercicio4_solid_hotel

import android.os.Build
import androidx.annotation.RequiresApi

class ReservationPriceCalculatorImpl(
    private val pricingStrategy: PricingStrategy = StandardPricing()
) : ReservationPriceCalculator {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun calculateTotal(room: Room, dateRange: DateRange): Double {
        val nights = dateRange.numberOfNights()
        return pricingStrategy.calculate(room.priceNight, nights)
    }
}