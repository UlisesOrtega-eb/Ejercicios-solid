package edu.itvo.ejercicio2solid_student

interface StudentRepository {
    fun add(student: Student)
    fun getById(id: String): Student?
}