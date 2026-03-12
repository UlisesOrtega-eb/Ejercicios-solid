package edu.itvo.ejercicio2solid_student

class Course(
    val code: String,
    val description: String,
    private val capacity: Int = 30
) {
    private val students = mutableSetOf<Student>()
    private var teacher: Teacher? = null

    fun assignTeacher(teacher: Teacher ){
        this.teacher = teacher
    }

    fun enrollStudent(student: Student): Boolean {
        if (students.size >= capacity) return false
        if (students.contains(student)) return false

        students.add(student)
        student.enroll(this)
        return true
    }

    fun getStudents(): Set<Student> = students
    fun getTeacher(): Teacher? = teacher
}