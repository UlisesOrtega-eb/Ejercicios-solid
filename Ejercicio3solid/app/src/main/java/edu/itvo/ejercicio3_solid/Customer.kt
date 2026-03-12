package edu.itvo.ejercicio3_solid;

data class Customer(
        val id: String,
        val name: String,
        val email: String,
        private val purchaseHistory: MutableList<Order> = mutableListOf()) {

    fun addOrder(order: Order){
        purchaseHistory.add(order)
    }
    fun getPurchaseHistory(): List<Order> = purchaseHistory.toList()
}