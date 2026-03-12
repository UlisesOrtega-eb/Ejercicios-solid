package edu.itvo.ejercicio3_solid

class OrderRepositoryInMemory : OrderRepository{

    private val orders = mutableListOf<Order>()

    override fun guardar(order: Order) {
        orders.add(order)
        order.customer.addOrder(order)
    }

    override fun getByCustomer(customerId: String): List<Order> {
        return orders.filter {it.customer.id == customerId}
    }
}