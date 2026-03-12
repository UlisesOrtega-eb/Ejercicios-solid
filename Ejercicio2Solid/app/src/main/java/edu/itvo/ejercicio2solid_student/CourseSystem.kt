package edu.itvo.ejercicio2solid_student

class CourseSystem(
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository,
    private val registrationService: RegistrationService
) {

    fun enrollCourse(course: Course) {
        courseRepository.add(course)
    }

    fun enrollStudent(student: Student) {
        studentRepository.add(student)
    }

    fun enrollStudentInCourse(idEstudiante: String, courseCode: String) {
        val student = studentRepository.getById(idEstudiante)
            ?: throw IllegalArgumentException("Student not exist")

        val isRegistration = registrationService.enroll(student, courseCode)

        if (!isRegistration) {
            println("❌ Registration is not possible (the course is full or duplicated)")
        }
    }

    fun showStudentsForCourse(courseCode: String) {
        val course = courseRepository.getByCode(courseCode) ?: return
        println("📘 Curso: ${course.description}")
        course.getStudents().forEach {
            println("- ${it.name}")
        }
    }
}
