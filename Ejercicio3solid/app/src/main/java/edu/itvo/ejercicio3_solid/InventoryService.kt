package edu.itvo.ejercicio3_solid

class InventoryService(private val products: MutableList<Product>) : Inventory{
    override fun checkAvailability(items: List<OrderItemDraft>): Boolean {
        return items.all { item->
            val product = products.find { it.code == item.productCode }
            product != null && product.stock >= item.quantity
        }
    }

    override fun reduceStock(items: List<OrderItemDraft>) {
        items.forEach { item->
            products.find {
                it.code == item.productCode}?.let {
                    it.stock -= item.quantity
                }
        }
    }

    override fun restore(items: List<OrderItemDraft>) {
        items.forEach { item->
            products.find { it.code == item.productCode }?.let {
                it.stock+=item.quantity
            }
        }
    }

}