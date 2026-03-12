package edu.itvo.ejercicio2solid_student

class RegistrationService(
    private val courseRepository: CourseRepository
) {

    fun enroll(student: Student, courseCode: String): Boolean {
        val course = courseRepository.getByCode(courseCode)
            ?: throw IllegalArgumentException("Course not exist")

        val coursesRegistered = getCoursesByStudent(student).filter { it.code==courseCode }

        check(coursesRegistered.isNotEmpty()) {
            "The student is already registered for that course"
        }

        return course.enrollStudent(student)

    }


    fun getCoursesByStudent(student: Student): List<Course> =
        courseRepository.getAll()
            .filter { it.getStudents().contains(student) }


}
