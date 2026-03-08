package edu.itvo.biblioteca

import android.os.Build
import androidx.annotation.RequiresApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SistemaBibliotecaTest {

    private lateinit var biblioteca: Biblioteca
    private lateinit var prestamos: Prestamos  // Cambiado de sistema a prestamos
    private lateinit var usuario: Usuario
    private lateinit var libro1: Libro
    private lateinit var libro2: Libro
    private lateinit var libro3: Libro
    private lateinit var libro4: Libro

    @Before
    fun setUp() {
        biblioteca = Biblioteca()
        prestamos = Prestamos(biblioteca)  // Cambiado de sistema a prestamos

        usuario = Usuario(nombre = "Usuario Test", id = 1)
        libro1 = Libro("Libro 1", "Autor A", "ISBN1")
        libro2 = Libro("Libro 2", "Autor B", "ISBN2")
        libro3 = Libro("Libro 3", "Autor C", "ISBN3")
        libro4 = Libro("Libro 4", "Autor D", "ISBN4")

        biblioteca.usuarios.add(usuario)
        biblioteca.libros.addAll(listOf(libro1, libro2, libro3, libro4))
    }

    @Test
    fun noSePuedePrestarLibroYaPrestado() {
        prestamos.prestarLibro(libro1, usuario)
        val autorizado = prestamos.autorizaPrestamo(usuario.id, libro1.isbn)
        assertFalse(autorizado)
    }

    @Test
    fun usuarioNoPuedeTenerMasDe3LibrosPrestados() {
        prestamos.prestarLibro(libro1, usuario)
        prestamos.prestarLibro(libro2, usuario)
        prestamos.prestarLibro(libro3, usuario)
        val autorizado = prestamos.autorizaPrestamo(usuario.id, libro4.isbn)
        assertFalse(autorizado)
    }

    @Test
    fun puedePrestarSiUsuarioTieneMenosDe3LibrosYLibroDisponible() {
        val autorizado = prestamos.autorizaPrestamo(usuario.id, libro1.isbn)
        assertTrue(autorizado)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Test
    fun devolverLibroLoMarcaComoDisponibleOtraVez() {
        prestamos.prestarLibro(libro1, usuario)
        prestamos.devolverLibro(libro1, usuario)
        assertTrue(libro1.disponible)
    }

    @Test
    fun mostrarLibrosDisponiblesSoloMuestraNoPrestados() {
        prestamos.prestarLibro(libro1, usuario)
        val disponibles = biblioteca.libros.filter { it.disponible }
        assertTrue(disponibles.containsAll(listOf(libro2, libro3, libro4)))
        assertFalse(disponibles.contains(libro1))
    }

    @Test
    fun mostrarLibrosEnPrestamoSoloMuestraPrestados() {
        prestamos.prestarLibro(libro2, usuario)
        val enPrestamo = biblioteca.libros.filter { !it.disponible }
        assertTrue(enPrestamo.contains(libro2))
        assertFalse(enPrestamo.contains(libro1))
    }
}