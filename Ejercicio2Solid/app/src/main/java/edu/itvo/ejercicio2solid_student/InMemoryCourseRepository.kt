package edu.itvo.ejercicio2solid_student

import kotlin.collections.set

class InMemoryCourseRepository: CourseRepository {
    private val courses = mutableMapOf<String, Course>()

    override fun add(course: Course) {
        courses[course.code] = course
    }

    override fun getByCode(code: String): Course? = courses[code]


    override fun getAll (): List<Course> =
        courses.values.toList()

}
