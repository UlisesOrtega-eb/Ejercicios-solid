package interfaces

import models.Activity
import models.Assistant

interface IEventsSystem {


        fun inscribirAsistente(asistente: Assistant, actividad: Activity): Boolean

}

