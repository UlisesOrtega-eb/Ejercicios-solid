package edu.itvo.ejercicio2solid_student

interface CourseRepository {
    fun add(course: Course)
    fun getByCode(code: String): Course?
    fun getAll(): List<Course>

}