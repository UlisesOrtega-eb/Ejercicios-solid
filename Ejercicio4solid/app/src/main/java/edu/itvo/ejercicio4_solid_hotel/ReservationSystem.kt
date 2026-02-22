package edu.itvo.ejercicio4_solid_hotel

class ReservationSystem(
    private val validator: ReservationValidator,
    private val availabilityChecker: RoomAvailabilityChecker,
    private val priceCalculator: ReservationPriceCalculator,
    private val inventoryManager: RoomInventoryManager,
    private val repository: ReservationRepository
) : ReservationCreator, ReservationCanceller, ReservationQuery {

    override fun createReservation(
        room: Room,
        guest: Guest,
        dateRange: DateRange
    ): ReservationResult {
        // 1. Validar
        val validationResult = validator.validate(room, guest, dateRange)
        if (!validationResult.isValid) {
            return ReservationResult(
                success = false,
                message = validationResult.errors.joinToString(", ")
            )
        }

        // 2. Calcular costo
        val totalCost = priceCalculator.calculateTotal(room, dateRange)

        // 3. Crear reserva
        val reservation = Reservation.create(room, guest, dateRange, totalCost)

        // 4. Guardar
        repository.save(reservation)

        // 5. Marcar habitación (opcional: puede hacerse al check-in)
        // inventoryManager.markAsOccupied(room)

        return ReservationResult(
            success = true,
            reservation = reservation,
            message = "Reserva creada exitosamente"
        )
    }

    override fun cancelReservation(reservationId: String): CancellationResult {
        val reservation = repository.findById(reservationId)
            ?: return CancellationResult(false, "Reserva no encontrada")

        if (reservation.status == ReservationStatus.CANCELLED) {
            return CancellationResult(false, "La reserva ya está cancelada")
        }

        if (reservation.status == ReservationStatus.COMPLETED) {
            return CancellationResult(false, "No se puede cancelar una reserva completada")
        }

        // Actualizar estado
        val updatedReservation = reservation.copy(status = ReservationStatus.CANCELLED)
        repository.update(updatedReservation)

        // Liberar habitación
        inventoryManager.markAsAvailable(reservation.room)

        return CancellationResult(true, "Reserva cancelada exitosamente")
    }

    override fun findReservation(id: String): Reservation? {
        return repository.findById(id)
    }

    override fun getGuestReservations(guestId: Int): List<Reservation> {
        return repository.findByGuest(guestId)
    }

    override fun getRoomReservations(roomNumber: String): List<Reservation> {
        return repository.findByRoom(roomNumber)
    }

    // Métodos adicionales útiles
    fun findAvailableRooms(dateRange: DateRange): List<Room> {
        return availabilityChecker.findAvailableRooms(dateRange)
    }

    fun findAvailableRoomsByType(type: RoomType, dateRange: DateRange): List<Room> {
        return availabilityChecker.findAvailableRoomsByType(type, dateRange)
    }
}
