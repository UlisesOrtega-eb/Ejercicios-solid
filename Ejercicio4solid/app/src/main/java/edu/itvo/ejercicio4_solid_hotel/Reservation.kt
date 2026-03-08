package edu.itvo.ejercicio4_solid_hotel

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.UUID

data class Reservation (
    val id: String,
    val room: Room,
    val guest: Guest,
    val dateRange: DateRange,
    val totalCost: Double,
    var status: ReservationStatus,
    val createdAt: LocalDate
){
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun create(
            room: Room,
            guest: Guest,
            dateRange: DateRange,
            totalCost: Double
        ): Reservation {
            return Reservation(
                id = UUID.randomUUID().toString(),
                room = room,
                guest = guest,
                dateRange = dateRange,
                totalCost = totalCost,
                status = ReservationStatus.CONFIRMED,
                createdAt = LocalDate.now()
            )
        }
    }
}