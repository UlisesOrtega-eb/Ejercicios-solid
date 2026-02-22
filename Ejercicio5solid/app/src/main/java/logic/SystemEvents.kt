package logic

import interfaces.IEventsSystem
import models.Activity
import models.Assistant

class SystemEvents(private val validador: ValidatorSchedule) : IEventsSystem {

    override fun inscribirAsistente(asistente: Assistant, actividad: Activity): Boolean {
        // 1. Validar cupo
        if (!validador.hayCupo(actividad)) {
            println("ERROR: Cupo lleno para la actividad ${actividad.nombre}")
            return false
        }

        // 2. Validar solapamiento de horario
        if (validador.haySolapamiento(actividad, asistente.actividadesInscritas)) {
            println("ERROR: El asistente ${asistente.nombre} ya tiene otra actividad a esa hora.")
            return false
        }

        // 3. Registrar inscripción
        actividad.inscritos.add(asistente)
        asistente.actividadesInscritas.add(actividad)
        println("ÉXITO: ${asistente.nombre} inscrito en ${actividad.nombre}")
        return true
    }


    fun obtenerAsistentesPorActividad(actividad: Activity): List<Assistant> {
        return actividad.inscritos
    }

    // Resumen: Filtra y ordena las actividades de un asistente por hora de inicio.
    fun obtenerCronogramaAsistente(asistente: Assistant): List<Activity> {
        return asistente.actividadesInscritas.sortedBy { it.horaInicio }
    }
}
