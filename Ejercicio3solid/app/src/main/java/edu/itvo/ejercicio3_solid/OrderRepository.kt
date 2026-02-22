package edu.itvo.ejercicio3_solid

interface OrderRepository {
    fun guardar(order: Order)
    fun getByCustomer(customerId: String ): List<Order>
}