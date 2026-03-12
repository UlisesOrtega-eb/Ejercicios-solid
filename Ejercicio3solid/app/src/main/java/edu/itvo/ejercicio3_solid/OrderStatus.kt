package edu.itvo.ejercicio3_solid

enum class OrderStatus {
    CREATED,        // Orden creada, pendiente de pago
    PENDING,        // Pendiente de confirmación
    CONFIRMED,      // Confirmada, lista para procesar
    PROCESSING,     // En proceso de preparación
    SHIPPED,        // Enviada
    DELIVERED,      // Entregada al cliente
    CANCELLED,      // Cancelada
    REFUNDED        // Reembolsada
}