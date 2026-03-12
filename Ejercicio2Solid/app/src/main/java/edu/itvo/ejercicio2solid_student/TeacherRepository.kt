package edu.itvo.ejercicio2solid_student

interface TeacherRepository {
    fun add(teacher: Teacher)
    //fun getByName(name: String): Teacher?
    fun getById(id: String): Teacher?
}