package edu.itvo.ejercicio2solid_student

class InMemoryStudentRepository: StudentRepository{
    private val students = mutableMapOf<String, Student>()

    override fun add(student: Student) {
        students[student.id] = student
    }

    override fun getById(id: String): Student? =
        students[id]

}