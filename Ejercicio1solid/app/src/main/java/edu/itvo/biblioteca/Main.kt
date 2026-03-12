package edu.itvo.biblioteca

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

data class Libro(
    val titulo: String,
    val autor: String,
    val isbn: String,
    var disponible: Boolean = true
)

data class Usuario(
    val nombre: String,
    val id: Int,
    val prestamos: MutableList<Prestamo> = mutableListOf()
)

data class Prestamo(
    val libro: Libro,
    val usuario: Usuario,
    val fechaPrestamo: LocalDate,
    var fechaDevolucion: LocalDate? = null
)

class Biblioteca {
    val libros = mutableListOf<Libro>()
    val usuarios = mutableListOf<Usuario>()
    val prestamos = mutableListOf<Prestamo>()
}

class Prestamos(val biblioteca: Biblioteca) {

    fun autorizaPrestamo(userId: Int, isbn: String): Boolean {
        val usuario = biblioteca.usuarios.find { it.id == userId } ?: return false
        val libroDisponible = biblioteca.libros.any { it.isbn == isbn && it.disponible }
        return usuario.prestamos.size < 3 && libroDisponible
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prestarLibro(libro: Libro, usuario: Usuario) {
        val prestamo = Prestamo(
            libro = libro,
            usuario = usuario,
            fechaPrestamo = LocalDate.now()
        )
        biblioteca.prestamos.add(prestamo)
        libro.disponible = false
        usuario.prestamos.add(prestamo)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun devolverLibro(libro: Libro, usuario: Usuario) {
        val prestamo = biblioteca.prestamos.find {
            it.libro == libro && it.usuario == usuario && it.fechaDevolucion == null
        }
        prestamo?.let {
            it.fechaDevolucion = LocalDate.now()
            usuario.prestamos.remove(it)
            libro.disponible = true
        }
    }
}

class ReporteBiblioteca(val biblioteca: Biblioteca) {
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
fun main() {
    val biblioteca = Biblioteca()
    biblioteca.usuarios.add(Usuario(nombre = "Ambrosio Cardoso", id = 1))

    biblioteca.libros.add(Libro(
        titulo = "Aprendiendo Kotlin en 21 días",
        autor = "Roman Leobardo",
        isbn = "AAA111",
        disponible = true
    ))
    biblioteca.libros.add(Libro(
        titulo = "Algorithms for Functional Programming",
        autor = "John David Stone",
        isbn = "AAA112",
        disponible = true
    ))
    biblioteca.libros.add(Libro(
        titulo = "Android Development with Kotlin",
        autor = "Marcin Moskala",
        isbn = "AAA113",
        disponible = true
    ))
    biblioteca.libros.add(Libro(
        titulo = "Effective Kotlin",
        autor = "Marcin Moskala",
        isbn = "AAA114",
        disponible = true
    ))
    biblioteca.libros.add(Libro(
        titulo = "Kotlin Coroutines",
        autor = "Filip Babic",
        isbn = "AAA115",
        disponible = true
    ))
    biblioteca.libros.add(Libro(
        titulo = "Kotlin Notes for Professionals",
        autor = "Stack OverFlow",
        isbn = "AAA116",
        disponible = true
    ))

    val isbn = "AAA116"
    val userId = 1

    val prestamos = Prestamos(biblioteca)

    prestamos.prestarLibro(libro = biblioteca.libros[4], usuario = biblioteca.usuarios[0])
    prestamos.prestarLibro(libro = biblioteca.libros[3], usuario = biblioteca.usuarios[0])
    prestamos.prestarLibro(libro = biblioteca.libros[2], usuario = biblioteca.usuarios[0])

    if (prestamos.autorizaPrestamo(userId = userId, isbn = isbn)) {
        prestamos.prestarLibro(
            libro = biblioteca.libros[5], // Cambiado a índice 5 para que sea el ISBN "AAA116"
            usuario = biblioteca.usuarios[0]
        )
        val reporteBiblioteca = ReporteBiblioteca(biblioteca)
        reporteBiblioteca.mostrarLibrosEnPrestamo()
    } else {
        println("Usuario $userId no se le puede prestar libro $isbn")
    }
}
