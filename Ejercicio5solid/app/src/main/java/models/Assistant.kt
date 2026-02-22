package models

data class Assistant(
    val nombre: String,
    val email: String,
    val actividadesInscritas: MutableList<Activity> = mutableListOf()
)