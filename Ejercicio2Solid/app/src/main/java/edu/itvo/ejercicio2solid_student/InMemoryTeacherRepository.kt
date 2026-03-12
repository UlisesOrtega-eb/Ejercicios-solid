package edu.itvo.ejercicio2solid_student

class InMemoryTeacherRepository: TeacherRepository {
    private val teachers = mutableMapOf<String, Teacher>()

    override fun add(teacher: Teacher) {
        teachers[teacher.id] = teacher
    }

    override fun getById(id: String): Teacher? = teachers[id]


}