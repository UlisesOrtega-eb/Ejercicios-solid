package edu.itvo.gestiondeeventos



import models.*
import logic.*
import org.junit.Test
import org.junit.Assert.*

class EventsTest {

    @Test
    fun `no debe permitir que un asistente se inscriba en actividades solapadas`() {
        val validador = ValidatorSchedule()
        val sistema = SystemEvents(validador)
        val ponente = Speaker("Dr. snow", "Ciencia de datos")

        // Actividad 1: 09:00 a 12:00
        val taller1 = Activity("Taller python", ponente, 900, 1200, 12)
        // Actividad 2: 11:00 a 13:00 (Se solapa una hora)
        val taller2 = Activity("Taller ciencia de datos", ponente, 1100, 1300, 12)

        val asistente = Assistant("Juan", "juan@gmail.com")

        sistema.inscribirAsistente(asistente, taller1)
        val resultadoFalla = sistema.inscribirAsistente(asistente, taller2)

        assertFalse("El sistema permitió inscribir una actividad solapada", resultadoFalla)
    }

    @Test
    fun `debe validar el cupo maximo por actividad`() {
        val validador = ValidatorSchedule()
        val sistema = SystemEvents(validador)

        val charla = Activity("Platica SOLID", Speaker("Bibiana", "Arquitectura"), 1500, 1600, 2)

        val a1 = Assistant("User 1", "u1@test.com")
        val a2 = Assistant("User 2", "u2@test.com")
        val a3 = Assistant("User 3", "u3@test.com")

        sistema.inscribirAsistente(a1, charla)
        sistema.inscribirAsistente(a2, charla)

        val resultado3 = sistema.inscribirAsistente(a3, charla)

        assertFalse("Se excedió el cupo máximo de la actividad", resultado3)
    }

    @Test
    fun `debe mostrar el cronograma ordenado por asistente`() {
        val validador = ValidatorSchedule()
        val sistema = SystemEvents(validador)
        val asistente = Assistant("Juan", "Perez @gmail.com")

        val tarde = Activity("Cierre", Speaker("P1", "D"), 1700, 1800, 20)
        val mañana = Activity("Apertura", Speaker("P1", "D"), 800, 900, 20)

        sistema.inscribirAsistente(asistente, tarde)
        sistema.inscribirAsistente(asistente, mañana)

        val cronograma = sistema.obtenerCronogramaAsistente(asistente)

        assertEquals("Apertura", cronograma[0].nombre)
        println("Cronograma de ${asistente.nombre}:")
        cronograma.forEach { println("${it.horaInicio} hrs - ${it.nombre}") }
    }
}