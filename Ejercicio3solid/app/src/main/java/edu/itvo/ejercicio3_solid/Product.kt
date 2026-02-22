package edu.itvo.ejercicio3_solid

data class Product(
    val code: String,
    val name : String,
    val category: String,
    val price : Double,
    var stock: Int,
    val taxeable: Boolean=true
)
