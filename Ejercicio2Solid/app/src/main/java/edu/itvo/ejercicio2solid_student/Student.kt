package edu.itvo.ejercicio2solid_student

class Student(
    val id: String,
    val name: String
){

    private val courses = mutableSetOf<Course>()

    fun enroll(course: Course){
        courses.add(course)
    }

    fun getCourses(): Set<Course> = courses
}