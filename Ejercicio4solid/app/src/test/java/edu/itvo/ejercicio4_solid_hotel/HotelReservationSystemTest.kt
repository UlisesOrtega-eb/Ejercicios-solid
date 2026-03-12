package edu.itvo.ejercicio4_solid_hotel

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate


class HotelReservationSystemTest {

    private lateinit var hotel: Hotel
    private lateinit var guest1: Guest
    private lateinit var guest2: Guest
    private lateinit var availabilityChecker: RoomAvailabilityChecker
    private lateinit var priceCalculator: ReservationPriceCalculator
    private lateinit var inventoryManager: RoomInventoryManager
    private lateinit var repository: ReservationRepository
    private lateinit var validator: ReservationValidator
    private lateinit var reservationSystem: ReservationSystem

    @Before
    fun setup() {
        // Setup hotel con habitaciones
        hotel = Hotel("Test Hotel")
        hotel.rooms.addAll(listOf(
            Room("101", RoomType.SINGLE, 500.0),
            Room("102", RoomType.SINGLE, 500.0),
            Room("201", RoomType.DOUBLE, 800.0),
            Room("202", RoomType.DOUBLE, 800.0),
            Room("301", RoomType.SUITE, 1500.0)
        ))

        // Setup huéspedes
        guest1 = Guest(1, "Carlos Martínez", "12345678A")
        guest2 = Guest(2, "Ana López", "87654321B")

        // Setup dependencias (DIP)
        availabilityChecker = RoomAvailabilityService(hotel)
        priceCalculator = ReservationPriceCalculatorImpl(StandardPricing())
        inventoryManager = RoomInventoryService(hotel)
        repository = ReservationRepositoryInMemory(hotel)

        val validationRules = listOf(
            RoomAvailabilityValidation(availabilityChecker),
            DateRangeValidation(),
            MaximumStayValidation(30)
        )
        validator = ReservationValidatorComposite(validationRules)

        reservationSystem = ReservationSystem(
            validator = validator,
            availabilityChecker = availabilityChecker,
            priceCalculator = priceCalculator,
            inventoryManager = inventoryManager,
            repository = repository
        )
    }

    // ========================================================================
    // ROOM TESTS
    // ========================================================================

    @Test
    fun testRoomCreation() {
        val room = Room("101", RoomType.SINGLE, 500.0)

        assertEquals("101", room.numberRoom)
        assertEquals(RoomType.SINGLE, room.roomType)
        assertEquals(500.0, room.priceNight, 0.01)
        assertTrue(room.availability)
    }

    @Test
    fun testRoomTypeHasCorrectProperties() {
        assertEquals("Individual", RoomType.SINGLE.displayName)
        assertEquals(500.0, RoomType.SINGLE.basePrice, 0.01)

        assertEquals("Suite", RoomType.SUITE.displayName)
        assertEquals(1500.0, RoomType.SUITE.basePrice, 0.01)
    }

    @Test
    fun testRoomDefaultAvailability() {
        val room = Room("999", RoomType.DELUXE, 3000.0)
        assertTrue(room.availability)
    }

    // ========================================================================
    // GUEST TESTS
    // ========================================================================

    @Test
    fun testGuestCreation() {
        val guest = Guest(100, "Test User", "11111111A")

        assertEquals(100, guest.id)
        assertEquals("Test User", guest.name)
        assertEquals("11111111A", guest.dni)
        assertTrue(guest.getReservationHistory().isEmpty())
    }

    // ========================================================================
    // DATE RANGE TESTS
    // ========================================================================

    @Test
    fun testDateRangeCreation() {
        val checkIn = LocalDate.now().plusDays(1)
        val checkOut = checkIn.plusDays(3)
        val dateRange = DateRange(checkIn, checkOut)

        assertEquals(checkIn, dateRange.checkIn)
        assertEquals(checkOut, dateRange.checkOut)
        assertEquals(3L, dateRange.numberOfNights())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDateRangeFailsWhenCheckOutBeforeCheckIn() {
        val checkIn = LocalDate.now().plusDays(5)
        val checkOut = LocalDate.now().plusDays(3)
        DateRange(checkIn, checkOut)
    }

    @Test
    fun testDateRangeDetectsOverlap() {
        val range1 = DateRange(
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15)
        )
        val range2 = DateRange(
            LocalDate.of(2024, 1, 12),
            LocalDate.of(2024, 1, 17)
        )

        assertTrue(range1.overlaps(range2))
        assertTrue(range2.overlaps(range1))
    }

    @Test
    fun testDateRangeNoOverlapWhenNonOverlapping() {
        val range1 = DateRange(
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15)
        )
        val range2 = DateRange(
            LocalDate.of(2024, 1, 16),
            LocalDate.of(2024, 1, 20)
        )

        assertFalse(range1.overlaps(range2))
        assertFalse(range2.overlaps(range1))
    }

    @Test
    fun testDateRangeNoOverlapWhenCheckOutEqualsCheckIn() {
        // Check-out del primero = Check-in del segundo (no se solapan)
        val range1 = DateRange(
            LocalDate.of(2024, 1, 10),
            LocalDate.of(2024, 1, 15)
        )
        val range2 = DateRange(
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 1, 20)
        )

        assertFalse(range1.overlaps(range2))
    }

    // ========================================================================
    // ROOM AVAILABILITY CHECKER TESTS
    // ========================================================================

    @Test
    fun testRoomIsAvailableWhenNoReservations() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )

        assertTrue(availabilityChecker.isAvailable(room, dateRange))
    }

    @Test
    fun testRoomIsNotAvailableWhenMarkedUnavailable() {
        val room = hotel.rooms[0]
        room.availability = false

        val dateRange = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )

        assertFalse(availabilityChecker.isAvailable(room, dateRange))
    }

    @Test
    fun testFindAvailableRoomsReturnsAllWhenNoReservations() {
        val dateRange = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )

        val availableRooms = availabilityChecker.findAvailableRooms(dateRange)

        assertEquals(5, availableRooms.size)
    }

    @Test
    fun testFindAvailableRoomsByTypeFiltersCorrectly() {
        val dateRange = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )

        val singleRooms = availabilityChecker.findAvailableRoomsByType(RoomType.SINGLE, dateRange)

        assertEquals(2, singleRooms.size)
        assertTrue(singleRooms.all { it.roomType == RoomType.SINGLE })
    }

    // ========================================================================
    // PRICE CALCULATOR TESTS
    // ========================================================================

    @Test
    fun testStandardPricingCalculatesCorrectly() {
        val room = Room("101", RoomType.SINGLE, 500.0)
        val dateRange = DateRange(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(4)
        )

        val total = priceCalculator.calculateTotal(room, dateRange)

        assertEquals(1500.0, total, 0.01) // 500 * 3 noches
    }

    @Test
    fun testBulkDiscountAppliesForLongStay() {
        val bulkCalculator = ReservationPriceCalculatorImpl(
            BulkDiscountPricing(minNights = 7, discountPercent = 0.15)
        )

        val room = Room("101", RoomType.SINGLE, 1000.0)
        val dateRange = DateRange(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(11)
        )

        val total = bulkCalculator.calculateTotal(room, dateRange)

        // 1000 * 10 noches = 10000, con 15% descuento = 8500
        assertEquals(8500.0, total, 0.01)
    }

    @Test
    fun testBulkDiscountNotAppliedForShortStay() {
        val bulkCalculator = ReservationPriceCalculatorImpl(
            BulkDiscountPricing(minNights = 7, discountPercent = 0.15)
        )

        val room = Room("101", RoomType.SINGLE, 1000.0)
        val dateRange = DateRange(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(4)
        )

        val total = bulkCalculator.calculateTotal(room, dateRange)

        // 1000 * 3 noches = 3000, sin descuento
        assertEquals(3000.0, total, 0.01)
    }

    // ========================================================================
    // VALIDATION TESTS
    // ========================================================================

    @Test
    fun testValidationPassesWithValidData() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )

        val result = validator.validate(room, guest1, dateRange)

        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun testValidationFailsWithPastDate() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().minusDays(5),
            LocalDate.now().minusDays(2)
        )

        val result = validator.validate(room, guest1, dateRange)

        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("anterior") })
    }

    @Test
    fun testValidationFailsWhenExceedsMaxStay() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(50)
        )

        val result = validator.validate(room, guest1, dateRange)

        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("máxima") })
    }

    // ========================================================================
    // INVENTORY MANAGER TESTS
    // ========================================================================

    @Test
    fun testMarkRoomAsOccupied() {
        val room = hotel.rooms[0]
        assertTrue(room.availability)

        inventoryManager.markAsOccupied(room)

        assertFalse(hotel.rooms[0].availability)
    }

    @Test
    fun testMarkRoomAsAvailable() {
        val room = hotel.rooms[0]
        room.availability = false

        inventoryManager.markAsAvailable(room)

        assertTrue(hotel.rooms[0].availability)
    }

    // ========================================================================
    // REPOSITORY TESTS
    // ========================================================================

    @Test
    fun testSaveReservationAddsToHotel() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(4)
        )
        val reservation = Reservation.create(room, guest1, dateRange, 1500.0)

        repository.save(reservation)

        assertEquals(1, hotel.reservation.size)
        assertEquals(reservation.id, hotel.reservation[0].id)
    }

    @Test
    fun testSaveReservationAddsToGuestHistory() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(4)
        )
        val reservation = Reservation.create(room, guest1, dateRange, 1500.0)

        repository.save(reservation)

        assertEquals(1, guest1.getReservationHistory().size)
        assertEquals(reservation.id, guest1.getReservationHistory()[0].id)
    }

    @Test
    fun testFindReservationById() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(4)
        )
        val reservation = Reservation.create(room, guest1, dateRange, 1500.0)
        repository.save(reservation)

        val found = repository.findById(reservation.id)

        assertNotNull(found)
        assertEquals(reservation.id, found?.id)
    }

    @Test
    fun testFindReservationByGuest() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(4)
        )
        val reservation = Reservation.create(room, guest1, dateRange, 1500.0)
        repository.save(reservation)

        val guestReservations = repository.findByGuest(guest1.id)

        assertEquals(1, guestReservations.size)
        assertEquals(guest1.id, guestReservations[0].guest.id)
    }

    @Test
    fun testFindReservationByRoom() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(4)
        )
        val reservation = Reservation.create(room, guest1, dateRange, 1500.0)
        repository.save(reservation)

        val roomReservations = repository.findByRoom(room.numberRoom)

        assertEquals(1, roomReservations.size)
        assertEquals(room.numberRoom, roomReservations[0].room.numberRoom)
    }

    @Test
    fun testUpdateReservationChangesStatus() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(4)
        )
        val reservation = Reservation.create(room, guest1, dateRange, 1500.0)
        repository.save(reservation)

        val updatedReservation = reservation.copy(status = ReservationStatus.CANCELLED)
        repository.update(updatedReservation)

        val found = repository.findById(reservation.id)
        assertEquals(ReservationStatus.CANCELLED, found?.status)
    }

    // ========================================================================
    // RESERVATION SYSTEM INTEGRATION TESTS
    // ========================================================================

    @Test
    fun testCreateReservationSuccess() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )

        val result = reservationSystem.createReservation(room, guest1, dateRange)

        assertTrue(result.success)
        assertNotNull(result.reservation)
        assertEquals("Reserva creada exitosamente", result.message)
        assertEquals(1500.0, result.reservation?.total!!, 0.01)
        assertEquals(ReservationStatus.CONFIRMED, result.reservation?.status)
    }

    @Test
    fun testCreateReservationCalculatesCostCorrectly() {
        val room = hotel.rooms[2] // Double room at 800.0/night
        val dateRange = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(10)
        )

        val result = reservationSystem.createReservation(room, guest1, dateRange)

        assertTrue(result.success)
        assertEquals(4000.0, result.reservation?.total!!, 0.01) // 800 * 5 nights
    }

    @Test
    fun testCreateReservationFailsWithOverlappingDates() {
        val room = hotel.rooms[0]
        val dateRange1 = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )
        val dateRange2 = DateRange(
            LocalDate.now().plusDays(6),
            LocalDate.now().plusDays(9)
        )

        reservationSystem.createReservation(room, guest1, dateRange1)
        val result = reservationSystem.createReservation(room, guest2, dateRange2)

        assertFalse(result.success)
        assertNull(result.reservation)
        assertTrue(result.message.contains("disponible"))
    }

    @Test
    fun testCreateReservationFailsWithPastDate() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().minusDays(5),
            LocalDate.now().minusDays(2)
        )

        val result = reservationSystem.createReservation(room, guest1, dateRange)

        assertFalse(result.success)
        assertTrue(result.message.contains("anterior"))
    }

    @Test
    fun testCancelReservationSuccess() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )
        val reservationResult = reservationSystem.createReservation(room, guest1, dateRange)
        val reservation = reservationResult.reservation!!

        val cancelResult = reservationSystem.cancelReservation(reservation.id)

        assertTrue(cancelResult.success)
        assertEquals("Reserva cancelada exitosamente", cancelResult.message)

        val cancelled = repository.findById(reservation.id)
        assertEquals(ReservationStatus.CANCELLED, cancelled?.status)
    }

    @Test
    fun testCancelReservationFailsWhenNotFound() {
        val result = reservationSystem.cancelReservation("INVALID_ID")

        assertFalse(result.success)
        assertEquals("Reserva no encontrada", result.message)
    }

    @Test
    fun testCancelReservationFailsWhenAlreadyCancelled() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )
        val reservationResult = reservationSystem.createReservation(room, guest1, dateRange)
        val reservation = reservationResult.reservation!!

        reservationSystem.cancelReservation(reservation.id)
        val result = reservationSystem.cancelReservation(reservation.id)

        assertFalse(result.success)
        assertTrue(result.message.contains("cancelada"))
    }

    @Test
    fun testCancelReservationFreesRoom() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )

        val reservationResult = reservationSystem.createReservation(room, guest1, dateRange)
        inventoryManager.markAsOccupied(room)
        assertFalse(hotel.rooms[0].availability)

        reservationSystem.cancelReservation(reservationResult.reservation!!.id)

        assertTrue(hotel.rooms[0].availability)
    }

    @Test
    fun testFindAvailableRoomsReducesAfterReservation() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )

        val availableBefore = reservationSystem.findAvailableRooms(dateRange)
        assertEquals(5, availableBefore.size)

        reservationSystem.createReservation(room, guest1, dateRange)

        val availableAfter = reservationSystem.findAvailableRooms(dateRange)
        assertEquals(4, availableAfter.size)
    }

    @Test
    fun testMultipleReservationsSameRoomDifferentDates() {
        val room = hotel.rooms[0]
        val dateRange1 = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )
        val dateRange2 = DateRange(
            LocalDate.now().plusDays(10),
            LocalDate.now().plusDays(13)
        )

        val result1 = reservationSystem.createReservation(room, guest1, dateRange1)
        val result2 = reservationSystem.createReservation(room, guest2, dateRange2)

        assertTrue(result1.success)
        assertTrue(result2.success)
        assertEquals(2, repository.findByRoom(room.numberRoom).size)
    }

    @Test
    fun testGetGuestReservations() {
        val room1 = hotel.rooms[0]
        val room2 = hotel.rooms[1]
        val dateRange1 = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )
        val dateRange2 = DateRange(
            LocalDate.now().plusDays(10),
            LocalDate.now().plusDays(13)
        )

        reservationSystem.createReservation(room1, guest1, dateRange1)
        reservationSystem.createReservation(room2, guest1, dateRange2)

        val reservations = reservationSystem.getGuestReservations(guest1.id)

        assertEquals(2, reservations.size)
        assertTrue(reservations.all { it.guest.id == guest1.id })
    }

    @Test
    fun testGetRoomReservations() {
        val room = hotel.rooms[0]
        val dateRange1 = DateRange(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(8)
        )
        val dateRange2 = DateRange(
            LocalDate.now().plusDays(10),
            LocalDate.now().plusDays(13)
        )

        reservationSystem.createReservation(room, guest1, dateRange1)
        reservationSystem.createReservation(room, guest2, dateRange2)

        val reservations = reservationSystem.getRoomReservations(room.numberRoom)

        assertEquals(2, reservations.size)
        assertTrue(reservations.all { it.room.numberRoom == room.numberRoom })
    }

    @Test
    fun testReservationFactoryMethodSetsCorrectDefaults() {
        val room = hotel.rooms[0]
        val dateRange = DateRange(
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(4)
        )

        val reservation = Reservation.create(room, guest1, dateRange, 1500.0)

        assertNotNull(reservation.id)
        assertEquals(room, reservation.room)
        assertEquals(guest1, reservation.guest)
        assertEquals(dateRange, reservation.dateRange)
        assertEquals(1500.0, reservation.total, 0.01)
        assertEquals(ReservationStatus.CONFIRMED, reservation.status)
        assertNotNull(reservation.createdAt)
    }
}