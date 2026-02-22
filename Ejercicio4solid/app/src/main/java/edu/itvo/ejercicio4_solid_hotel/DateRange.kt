package edu.itvo.ejercicio4_solid_hotel

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
data class DateRange (
    val checkIn: LocalDate,
    val checkOut: LocalDate
){
    init {
        require(checkOut.isAfter(checkIn)){
            "la fecha de salida debe ser posterior a la fecha de entrada"
        }
    }

    fun numberOfNights(): Long = ChronoUnit.DAYS.between(checkIn,checkOut)

    fun overlaps(other: DateRange): Boolean{
        return !(checkOut.isBefore(other.checkIn) || other.checkOut.isBefore(checkIn)
                || checkOut.isEqual(other.checkIn) || other.checkOut.isEqual(checkIn))
    }
}