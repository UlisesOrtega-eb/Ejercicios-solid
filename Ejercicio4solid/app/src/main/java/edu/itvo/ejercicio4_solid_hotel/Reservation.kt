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
    val total: Double,
    var status: ReservationStatus,
    val createdAt: LocalDate
){
    companion object{
        @RequiresApi(Build.VERSION_CODES.O)
        fun create(
            room: Room,
            guest: Guest,
            dateRange: DateRange,
            total: Double
        ): Reservation{
            return Reservation(
                id = UUID.randomUUID().toString(),
                room =room,
                guest =guest,
                dateRange =dateRange,
                total =total,
                status = ReservationStatus.CONFIRMED,
                createdAt = LocalDate.now()
            )
        }
    }
}