package edu.itvo.ejercicio2solid_student

import org.junit.Test
import org.junit.Before
import org.junit.Assert.*
import org.mockito.kotlin.*

class EnrollmentServiceTest {

    private lateinit var courseRepository: CourseRepository
    private lateinit var studentRepository: StudentRepository
    private lateinit var enrollmentService: EnrollmentService

    @Before
    fun setUp() {
        courseRepository = mock()
        studentRepository = mock()
        enrollmentService = EnrollmentService(courseRepository, studentRepository)
    }

    @Test
    fun enrollshouldsuccessfullyenrollastudentinacourse() {
        // Arrange
        val studentId = "S001"
        val courseCode = "CS101"
        val student = mock<Student>()
        val course = mock<Course>()

        whenever(studentRepository.getById(studentId)).thenReturn(student)
        whenever(courseRepository.getByCode(courseCode)).thenReturn(course)
        whenever(course.enrollStudent(student)).thenReturn(true)

        // Act
        enrollmentService.enroll(studentId, courseCode)

        // Assert
        verify(studentRepository).getById(studentId)
        verify(courseRepository).getByCode(courseCode)
        verify(course).enrollStudent(student)
    }

    @Test(expected = IllegalArgumentException::class)
    fun enrollshouldthrowIllegalArgumentExceptionwhenstudentnotfound() {
        // Arrange
        val studentId = "S999"
        val courseCode = "CS101"

        whenever(studentRepository.getById(studentId)).thenReturn(null)

        // Act
        enrollmentService.enroll(studentId, courseCode)
    }

    @Test(expected = IllegalArgumentException::class)
    fun enrollshouldthrowIllegalArgumentExceptionwhencoursenotfound() {
        // Arrange
        val studentId = "S001"
        val courseCode = "CS999"
        val student = mock<Student>()

        whenever(studentRepository.getById(studentId)).thenReturn(student)
        whenever(courseRepository.getByCode(courseCode)).thenReturn(null)

        // Act
        enrollmentService.enroll(studentId, courseCode)
    }

    @Test(expected = IllegalStateException::class)
    fun enrollshouldthrowIllegalStateExceptionwhenenrollmentfails() {
        // Arrange
        val studentId = "S001"
        val courseCode = "CS101"
        val student = mock<Student>()
        val course = mock<Course>()

        whenever(studentRepository.getById(studentId)).thenReturn(student)
        whenever(courseRepository.getByCode(courseCode)).thenReturn(course)
        whenever(course.enrollStudent(student)).thenReturn(false)

        // Act
        enrollmentService.enroll(studentId, courseCode)
    }

    @Test
    fun `showCoursesByStudent should display all courses for a student`() {
        // Arrange
        val student = mock<Student>()
        val course1 = mock<Course>()
        val course2 = mock<Course>()
        val courses = setOf(course1, course2)

        whenever(student.name).thenReturn("Juan Pérez")
        whenever(student.getCourses()).thenReturn(courses)
        whenever(course1.description).thenReturn("Programming 101")
        whenever(course2.description).thenReturn("Data Structures")

        // Act
        enrollmentService.showCoursesByStudent(student)

        // Assert
        verify(student).name
        verify(student).getCourses()
        verify(course1).description
        verify(course2).description
    }

    @Test
    fun `showStudentByCourse should display all students in a course`() {
        // Arrange
        val course = mock<Course>()
        val student1 = mock<Student>()
        val student2 = mock<Student>()
        val students = setOf(student1, student2)

        whenever(course.description).thenReturn("Programming 101")
        whenever(course.getStudents()).thenReturn(students)
        whenever(student1.name).thenReturn("Juan Pérez")
        whenever(student2.name).thenReturn("María García")

        // Act
        enrollmentService.showStudentByCourse(course)

        // Assert
        verify(course).description
        verify(course).getStudents()
        verify(student1).name
        verify(student2).name
    }
}