package logic

import models.Activity

class ValidatorSchedule {
    // Resumen: Verifica si los horarios de dos actividades se cruzan.
    fun haySolapamiento(nueva: Activity, inscritas: List<Activity>): Boolean {
        return inscritas.any { existente ->
            nueva.horaInicio < existente.horaFin && nueva.horaFin > existente.horaInicio
        }
    }

    fun hayCupo(actividad: Activity): Boolean = actividad.inscritos.size < actividad.cupoMaximo
}

