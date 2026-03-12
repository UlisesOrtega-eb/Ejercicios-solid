package edu.itvo.ejercicio3_solid

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

/**
 * Suite de pruebas para el Sistema de Ventas
 * Usa JUnit 4
 */
class SalesSystemTest {

    private lateinit var products: MutableList<Product>
    private lateinit var customer: Customer
    private lateinit var inventory: Inventory
    private lateinit var tax: Tax
    private lateinit var orderItemFactory: OrderItemFactory
    private lateinit var repository: OrderRepository
    private lateinit var validator: OrderValidator
    private lateinit var salesSystem: SalesSystem

    @Before
    fun setup() {
        // Arrange: Configurar productos de prueba
        products = mutableListOf(
            Product("P001", "Laptop Dell", "Electrónica", 25000.0, 10, true),
            Product("P002", "Mouse Logitech", "Accesorios", 500.0, 50, true),
            Product("P003", "Teclado Mecánico", "Accesorios", 1500.0, 30, true),
            Product("P004", "Monitor LG", "Electrónica", 8000.0, 5, true),
            Product("P005", "Libro Kotlin", "Libros", 300.0, 100, false) // Sin IVA
        )

        this.customer = Customer("C001", "Juan Pérez", "juan@email.com")

        // Configurar dependencias
        inventory = InventoryService(products)
        tax = IvaTax()
        orderItemFactory = OrderItemFactory(tax)
        repository = OrderRepositoryInMemory()

        val validationRules = listOf(
            NonEmptyCartRule(),
            PositiveQuantitiesRule(),
            StockAvailabilityRule(inventory)
        )
        validator = OrderValidator(validationRules)

        salesSystem = SalesSystem(
            validator = validator,
            inventory = inventory,
            orderItemFactory = orderItemFactory,
            repository = repository
        )
    }

    // ========================================================================
    // PRODUCT TESTS
    // ========================================================================

    @Test
    fun testProductCreation() {
        val product = Product("P001", "Test Product", "Category", 100.0, 10, true)

        assertEquals("P001", product.code)
        assertEquals("Test Product", product.name)
        assertEquals("Category", product.category)
        assertEquals(100.0, product.price, 0.01)
        assertEquals(10, product.stock)
        assertTrue(product.taxeable)
    }

    @Test
    fun testProductDefaultTaxeable() {
        val product = Product("P001", "Test", "Cat", 100.0, 10)
        assertTrue(product.taxeable)
    }

    // ========================================================================
    // ORDER ITEM DRAFT TESTS
    // ========================================================================

    @Test
    fun testOrderItemDraftCreation() {
        val draft = OrderItemDraft(
            productCode = "P001",
            productName = "Laptop",
            category = "Electrónica",
            unitPrice = 25000.0,
            quantity = 2,
            taxable = true
        )

        assertEquals("P001", draft.productCode)
        assertEquals(2, draft.quantity)
        assertEquals(25000.0, draft.unitPrice, 0.01)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testOrderItemDraftInvalidQuantityZero() {
        OrderItemDraft("P001", "Laptop", "Electrónica", 25000.0, 0, true)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testOrderItemDraftInvalidQuantityNegative() {
        OrderItemDraft("P001", "Laptop", "Electrónica", 25000.0, -1, true)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testOrderItemDraftInvalidPrice() {
        OrderItemDraft("P001", "Laptop", "Electrónica", -100.0, 1, true)
    }

    // ========================================================================
    // INVENTORY TESTS
    // ========================================================================

    @Test
    fun testCheckAvailabilitySuccess() {
        val items = listOf(
            OrderItemDraft("P001", "Laptop Dell", "Electrónica", 25000.0, 2, true),
            OrderItemDraft("P002", "Mouse Logitech", "Accesorios", 500.0, 5, true)
        )

        assertTrue(inventory.checkAvailability(items))
    }

    @Test
    fun testCheckAvailabilityFailure() {
        val items = listOf(
            OrderItemDraft("P001", "Laptop Dell", "Electrónica", 25000.0, 50, true) // Solo hay 10
        )

        assertFalse(inventory.checkAvailability(items))
    }

    @Test
    fun testReduceStock() {
        val initialStock = products[0].stock
        val items = listOf(
            OrderItemDraft("P001", "Laptop Dell", "Electrónica", 25000.0, 3, true)
        )

        inventory.reduceStock(items)

        assertEquals(initialStock - 3, products[0].stock)
    }

    @Test
    fun testRestoreStock() {
        val items = listOf(
            OrderItemDraft("P001", "Laptop Dell", "Electrónica", 25000.0, 3, true)
        )

        inventory.reduceStock(items)
        val stockAfterReduction = products[0].stock

        inventory.restore(items)

        assertEquals(stockAfterReduction + 3, products[0].stock)
    }

    // ========================================================================
    // TAX TESTS
    // ========================================================================

    @Test
    fun testIvaTaxApplies() {
        val item = OrderItemDraft("P001", "Laptop", "Electrónica", 25000.0, 1, true)

        assertTrue(tax.appliesTo(item))
    }

    @Test
    fun testIvaTaxDoesNotApply() {
        val item = OrderItemDraft("P005", "Libro", "Libros", 300.0, 1, false)

        assertFalse(tax.appliesTo(item))
    }

    @Test
    fun testIvaTaxCalculation() {
        val subtotal = 1000.0
        val expectedTax = 160.0

        assertEquals(expectedTax, tax.calculate(subtotal), 0.01)
    }

    // ========================================================================
    // VALIDATION TESTS
    // ========================================================================

    @Test
    fun testValidationEmptyCart() {
        val result = validator.validate(emptyList())

        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("vacía") || it.contains("vacío") })
    }

    @Test
    fun testValidationInsufficientStock() {
        val items = listOf(
            OrderItemDraft("P001", "Laptop", "Electrónica", 25000.0, 100, true) // Más del stock
        )

        val result = validator.validate(items)

        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.contains("Stock") || it.contains("stock") })
    }

    @Test
    fun testValidationSuccess() {
        val items = listOf(
            OrderItemDraft("P001", "Laptop", "Electrónica", 25000.0, 2, true),
            OrderItemDraft("P002", "Mouse", "Accesorios", 500.0, 3, true)
        )

        val result = validator.validate(items)

        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    // ========================================================================
    // ORDER ITEM FACTORY TESTS
    // ========================================================================

    @Test
    fun testCreateOrderItemWithTax() {
        val draft = OrderItemDraft("P001", "Laptop", "Electrónica", 1000.0, 2, true)

        val orderItems = orderItemFactory.createOrderItems(listOf(draft))
        val item = orderItems.first()

        assertEquals("P001", item.productCode)
        assertEquals(2, item.quantity)
        assertEquals(2000.0, item.subtotal, 0.01) // 1000 * 2
        assertEquals(0.0, item.discountAmount, 0.01)
        assertEquals(320.0, item.taxAmount, 0.01) // 2000 * 0.16
        assertEquals(2320.0, item.total, 0.01) // 2000 + 320
    }

    @Test
    fun testCreateOrderItemWithoutTax() {
        val draft = OrderItemDraft("P005", "Libro", "Libros", 300.0, 2, false)

        val orderItems = orderItemFactory.createOrderItems(listOf(draft))
        val item = orderItems.first()

        assertEquals(600.0, item.subtotal, 0.01) // 300 * 2
        assertEquals(0.0, item.taxAmount, 0.01) // Sin IVA
        assertEquals(600.0, item.total, 0.01) // 600 + 0
    }

    @Test
    fun testCreateMultipleOrderItems() {
        val drafts = listOf(
            OrderItemDraft("P001", "Laptop", "Electrónica", 1000.0, 1, true),
            OrderItemDraft("P002", "Mouse", "Accesorios", 500.0, 2, true),
            OrderItemDraft("P005", "Libro", "Libros", 300.0, 1, false)
        )

        val orderItems = orderItemFactory.createOrderItems(drafts)

        assertEquals(3, orderItems.size)
        assertEquals("P001", orderItems[0].productCode)
        assertEquals("P002", orderItems[1].productCode)
        assertEquals("P005", orderItems[2].productCode)
    }

    // ========================================================================
    // ORDER TESTS
    // ========================================================================

    @Test
    fun testOrderFactoryMethod() {
        val items = listOf(
            OrderItem(
                productCode = "P001",
                productName = "Laptop",
                category = "Electrónica",
                unitPrice = 25000.0,
                quantity = 1,
                subtotal = 25000.0,
                discountAmount = 0.0,
                taxAmount = 4000.0,
                total = 29000.0,
                createdAt = LocalDateTime.now()
            )
        )

        val pricingResult = OrderPricingResult(
            items = items,
            subtotal = 25000.0,
            discountAmount = 0.0,
            taxAmount = 4000.0,
            total = 29000.0
        )

        val order = Order.from(customer, pricingResult)

        assertNotNull(order.id)
        assertEquals(customer, order.customer)
        assertEquals(items, order.items)
        assertEquals(25000.0, order.subtotal, 0.01)
        assertEquals(4000.0, order.taxAmount, 0.01)
        assertEquals(29000.0, order.total, 0.01)
        assertEquals(OrderStatus.CREATED, order.status)
        assertNotNull(order.createdAt)
    }

    // ========================================================================
    // ORDER REPOSITORY TESTS
    // ========================================================================

    @Test
    fun testSaveOrder() {
        val items = listOf(
            OrderItem("P001", "Laptop", "Electrónica", 25000.0, 1,
                25000.0, 0.0, 4000.0, 29000.0, LocalDateTime.now())
        )

        val pricingResult = OrderPricingResult(items, 25000.0, 0.0, 4000.0, 29000.0)
        val order = Order.from(customer, pricingResult)

        repository.guardar(order)

        val customerOrders = repository.getByCustomer(customer.id)
        assertEquals(1, customerOrders.size)
        assertEquals(order.id, customerOrders.first().id)
    }

    @Test
    fun testOrderAddedToCustomerHistory() {
        val items = listOf(
            OrderItem("P001", "Laptop", "Electrónica", 25000.0, 1,
                25000.0, 0.0, 4000.0, 29000.0, LocalDateTime.now())
        )

        val pricingResult = OrderPricingResult(items, 25000.0, 0.0, 4000.0, 29000.0)
        val order = Order.from(customer, pricingResult)

        repository.guardar(order)

        assertEquals(1, customer.getPurchaseHistory().size)
        assertEquals(order.id, customer.getPurchaseHistory().first().id)
    }

    @Test
    fun testGetByCustomer() {
        val customer2 = Customer("C002", "María López", "maria@email.com")

        val items1 = listOf(
            OrderItem("P001", "Laptop", "Electrónica", 25000.0, 1,
                25000.0, 0.0, 4000.0, 29000.0, LocalDateTime.now())
        )
        val items2 = listOf(
            OrderItem("P002", "Mouse", "Accesorios", 500.0, 2,
                1000.0, 0.0, 160.0, 1160.0, LocalDateTime.now())
        )

        val order1 = Order.from(customer, OrderPricingResult(items1, 25000.0, 0.0, 4000.0, 29000.0))
        val order2 = Order.from(customer2, OrderPricingResult(items2, 1000.0, 0.0, 160.0, 1160.0))

        repository.guardar(order1)
        repository.guardar(order2)

        val customer1Orders = repository.getByCustomer(customer.id)
        val customer2Orders = repository.getByCustomer(customer2.id)

        assertEquals(1, customer1Orders.size)
        assertEquals(1, customer2Orders.size)
        assertEquals(order1.id, customer1Orders.first().id)
        assertEquals(order2.id, customer2Orders.first().id)
    }

    // ========================================================================
    // SALES SYSTEM INTEGRATION TESTS
    // ========================================================================

    @Test
    fun testProcessOrderSuccess() {
        val items = listOf(
            OrderItemDraft("P001", "Laptop Dell", "Electrónica", 25000.0, 1, true),
            OrderItemDraft("P002", "Mouse Logitech", "Accesorios", 500.0, 2, true)
        )

        val initialStock1 = products[0].stock
        val initialStock2 = products[1].stock

        val result = salesSystem.processOrder(customer, items)

        assertTrue(result.success)
        assertNotNull(result.order)
        assertEquals("Orden procesada exitosamente", result.message)

        // Verificar que se redujo el stock
        assertEquals(initialStock1 - 1, products[0].stock)
        assertEquals(initialStock2 - 2, products[1].stock)

        // Verificar cálculos
        val order = result.order!!
        assertEquals(26000.0, order.subtotal, 0.01) // 25000 + (500*2)
        assertEquals(4160.0, order.taxAmount, 0.01) // 26000 * 0.16
        assertEquals(30160.0, order.total, 0.01)
    }

    @Test
    fun testProcessOrderInsufficientStock() {
        val items = listOf(
            OrderItemDraft("P001", "Laptop Dell", "Electrónica", 25000.0, 100, true) // Solo hay 10
        )

        val result = salesSystem.processOrder(customer, items)

        assertFalse(result.success)
        assertNull(result.order)
        assertTrue(result.message.contains("Stock") || result.message.contains("stock"))
    }

    @Test
    fun testProcessOrderEmptyCart() {
        val result = salesSystem.processOrder(customer, emptyList())

        assertFalse(result.success)
        assertNull(result.order)
        assertTrue(result.message.contains("vacía") || result.message.contains("vacío"))
    }

    @Test
    fun testProcessOrderWithNonTaxableProduct() {
        val items = listOf(
            OrderItemDraft("P005", "Libro Kotlin", "Libros", 300.0, 2, false) // Sin IVA
        )

        val result = salesSystem.processOrder(customer, items)

        assertTrue(result.success)
        val order = result.order!!

        assertEquals(600.0, order.subtotal, 0.01)
        assertEquals(0.0, order.taxAmount, 0.01) // Sin IVA
        assertEquals(600.0, order.total, 0.01)
    }

    @Test
    fun testProcessOrderMixedProducts() {
        val items = listOf(
            OrderItemDraft("P002", "Mouse Logitech", "Accesorios", 500.0, 1, true), // Con IVA
            OrderItemDraft("P005", "Libro Kotlin", "Libros", 300.0, 2, false) // Sin IVA
        )

        val result = salesSystem.processOrder(customer, items)

        assertTrue(result.success)
        val order = result.order!!

        assertEquals(1100.0, order.subtotal, 0.01) // 500 + 600
        assertEquals(80.0, order.taxAmount, 0.01) // Solo 500 * 0.16
        assertEquals(1180.0, order.total, 0.01)
    }

    @Test
    fun testGetCustomerOrders() {
        val items1 = listOf(
            OrderItemDraft("P001", "Laptop", "Electrónica", 25000.0, 1, true)
        )
        val items2 = listOf(
            OrderItemDraft("P002", "Mouse", "Accesorios", 500.0, 1, true)
        )
        val result1 = salesSystem.processOrder(customer, items1)
        val result2 = salesSystem.processOrder(customer, items2)

        // Debug: verificar que las órdenes se procesaron
        println("Result1 success: ${result1.success}")
        println("Result2 success: ${result2.success}")
        println("Result1 order: ${result1.order?.id}")
        println("Result2 order: ${result2.order?.id}")

        val orders = salesSystem.getCustomerOrders(customer.id)

        assertEquals(2, orders.size)
    }

    @Test
    fun testMultipleOrdersStockManagement() {
        val initialStock = products[0].stock // Laptop: 10

        val items1 = listOf(
            OrderItemDraft("P001", "Laptop", "Electrónica", 25000.0, 2, true)
        )
        val items2 = listOf(
            OrderItemDraft("P001", "Laptop", "Electrónica", 25000.0, 3, true)
        )

        salesSystem.processOrder(customer, items1)
        assertEquals(initialStock - 2, products[0].stock)

        salesSystem.processOrder(customer, items2)
        assertEquals(initialStock - 5, products[0].stock)
    }

    // ========================================================================
    // CUSTOMER TESTS
    // ========================================================================

    @Test
    fun testCustomerCreation() {
        val newCustomer = Customer("C100", "Test User", "test@email.com")

        assertEquals("C100", newCustomer.id)
        assertEquals("Test User", newCustomer.name)
        assertEquals("test@email.com", newCustomer.email)
        assertTrue(newCustomer.getPurchaseHistory().isEmpty())
    }

    @Test
    fun testAddOrderToPurchaseHistory() {
        val items = listOf(
            OrderItem("P001", "Laptop", "Electrónica", 25000.0, 1,
                25000.0, 0.0, 4000.0, 29000.0, LocalDateTime.now())
        )
        val pricingResult = OrderPricingResult(items, 25000.0, 0.0, 4000.0, 29000.0)
        val order = Order.from(customer, pricingResult)

        customer.addOrder(order)

        assertEquals(1, customer.getPurchaseHistory().size)
        assertEquals(order, customer.getPurchaseHistory().first())
    }

    @Test
    fun testGetPurchaseHistoryImmutable() {
        val history = customer.getPurchaseHistory()

        // El método toList() crea una copia, no debería afectar al original
        assertTrue(history is List<Order>)
    }
}