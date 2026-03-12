package edu.itvo.ejercicio3_solid

class SalesSystem(
    private val validator: OrderValidator,
    private val inventory: Inventory,
    private val orderItemFactory: OrderItemFactory,
    private val repository: OrderRepository
) {

    fun processOrder(customer: Customer, items: List<OrderItemDraft>): PurchaseResult {
        // 1. Validar
        val validationResult = validator.validate(items)
        if (!validationResult.isValid) {
            return PurchaseResult(
                success = false,
                message = validationResult.errors.joinToString(", ")
            )
        }

        // 2. Crear OrderItems
        val orderItems = orderItemFactory.createOrderItems(items)

        // 3. Calcular totales
        val pricingResult = calculateOrderPricing(orderItems)

        // 4. Reducir inventario
        inventory.reduceStock(items)

        // 5. Crear y guardar orden
        val order = Order.from(customer, pricingResult)
        repository.guardar(order)

        return PurchaseResult(
            success = true,
            order = order,
            message = "Orden procesada exitosamente"
        )
    }

    private fun calculateOrderPricing(items: List<OrderItem>): OrderPricingResult {
        return OrderPricingResult(
            items = items,
            subtotal = items.sumOf { it.subtotal },
            discountAmount = items.sumOf { it.discountAmount },
            taxAmount = items.sumOf { it.taxAmount },
            total = items.sumOf { it.total }
        )
    }

    fun getCustomerOrders(customerId: String): List<Order> {
        return repository.getByCustomer(customerId)
    }
}