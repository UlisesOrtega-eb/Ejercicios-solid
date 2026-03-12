package edu.itvo.ejercicio2solid_student

class EnrollmentService(
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository)
{
    fun enroll (studentId:String, courseCode: String){
        val student = studentRepository.getById(studentId)
            ?: throw IllegalArgumentException("Student not found")

        val course = courseRepository.getByCode(courseCode)
            ?:throw IllegalArgumentException("Course not found ")

        if (!course.enrollStudent(student)){
            throw IllegalStateException("Enrollment failed: duplicated or full course")
        }
    }

    fun showCoursesByStudent(student: Student){
        println(" Cursos de ${student.name}")
        student.getCourses().forEach {
            println("- ${it.description}")
        }
    }

    fun showStudentByCourse(course: Course){
        println("   Curso: ${course.description}")
        course.getStudents().forEach {
            println("-  ${it.name}")
        }
    }

}