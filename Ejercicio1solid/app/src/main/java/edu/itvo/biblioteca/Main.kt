package edu.itvo.biblioteca


    import android.os.Build
            import androidx.annotation.RequiresApi
            import java.time.LocalDate

    data class Libro(val titulo: String,
                             val autor: String,
                             val isbn: String,
                             var disponible: Boolean = true)

    data class Usuario(val nombre: String,
                       val id: Int,
                       val prestamos: MutableList<Prestamo> = mutableListOf())

    data class Prestamo(val libro: Libro,
                        val usuario: Usuario,
                        val fechaPrestamo: LocalDate,
                        var fechaDevolucion: LocalDate? = null)

    class Biblioteca
    {
        val libros = mutableListOf<Libro>()
        val usuarios = mutableListOf<Usuario>()
        val prestamos = mutableListOf<Prestamo>()


    }

    class Prestamos (val biblioteca: Biblioteca){
        fun autorizaPrestamo(userId: Int, isbn: String): Boolean {

            return (( biblioteca.usuarios.first({ it.id==userId}).prestamos.size <3)
                    && biblioteca.libros.count { it.disponible && it.isbn==isbn }>=1)
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun prestarLibro(libro: Libro, usuario: Usuario) {
            val prestamo = Prestamo(libro=libro,
                usuario=usuario,
                fechaPrestamo = LocalDate.now())
            biblioteca.prestamos.add(prestamo)

            biblioteca.libros.first { it == libro }.disponible = false
            biblioteca.usuarios.first { it == usuario }.prestamos.add(prestamo)
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun devolverLibro(libro: Libro, usuario: Usuario) {
            val prestamo= biblioteca.prestamos.first({ it.libro==libro
                    && it.usuario==usuario })
            prestamo.fechaDevolucion = LocalDate.now()
            biblioteca.usuarios.first { it.id == usuario.id }.prestamos.remove(prestamo)

            biblioteca.libros.find {it==libro} ?.disponible = true  // para que la prueba test de true

        }

    }

    class ReporteBiblioteca (val biblioteca: Biblioteca){
        fun mostrarLibrosDisponibles() {
            println("Lista de libros disponibles:\n")
            biblioteca.libros.filter { it.disponible }.forEach { println(it) }
        }
        fun mostrarLibrosEnPrestamo() {
            println("Lista de libros en préstamo:\n")
            biblioteca.libros.filter { !it.disponible }.forEach { println(it) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun main(){
        val biblioteca = Biblioteca()
        biblioteca.usuarios.add(Usuario(nombre = "Ambrosio Cardoso", id = 1 ))

        biblioteca.libros.add(Libro(titulo = "Aprendiendo Kotlin en 21 días",
            autor = "Roman Leobardo",
            isbn = "AAA111",
            disponible = true))
        biblioteca.libros.add(Libro(titulo = "Algorithms for Functional Programming",
            autor = "John David Stone",
            isbn = "AAA112",
            disponible = true))
        biblioteca.libros.add(Libro(titulo = "Android Development with Kotlin",
            autor = "Marcin Moskala",
            isbn = "AAA113",
            disponible = true))
        biblioteca.libros.add(Libro(titulo = "Effective Kotlin",
            autor = "Marcin Moskala",
            isbn = "AAA114",
            disponible = true))
        biblioteca.libros.add(Libro(titulo = "Kotlin Coroutines",
            autor = "Filip Babic",
            isbn = "AAA115",
            disponible = true))
        biblioteca.libros.add(Libro(titulo = "Kotlin Notes for Professionals",
            autor = "Stack OverFlow",
            isbn = "AAA116",
            disponible = true))

        val isbn = "AAA116" //biblioteca.libros[5]
        val userId =1

        val Prestamos = Prestamos(biblioteca)
        Prestamos.prestarLibro(libro=biblioteca.libros[4],
            usuario = biblioteca.usuarios[0] )
        Prestamos.prestarLibro(libro=biblioteca.libros[3],
            usuario = biblioteca.usuarios[0] )
        Prestamos.prestarLibro(libro=biblioteca.libros[2],
            usuario = biblioteca.usuarios[0] )

        if (Prestamos.autorizaPrestamo(userId = userId, isbn = isbn )) {
            Prestamos.prestarLibro(
                libro = biblioteca.libros[4],
                usuario = biblioteca.usuarios[0]
            )
            val reporteBiblioteca = ReporteBiblioteca(biblioteca)
            reporteBiblioteca.mostrarLibrosEnPrestamo()
        } else {
            println("Usuario $userId no se le puede prestar libro $isbn")
        }

    }






