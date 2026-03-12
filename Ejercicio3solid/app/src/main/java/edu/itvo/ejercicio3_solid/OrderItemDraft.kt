package edu.itvo.ejercicio3_solid

data class OrderItemDraft(
    val productCode: String,
    val productName:String,
    val category: String,
    val unitPrice: Double,
    val quantity:Int,
    val taxable: Boolean
)
{
    init {
        require(quantity > 0){"Quantity must be greater than ZERO"}
        require(unitPrice>=0){"Unit price be positive"}
    }
}