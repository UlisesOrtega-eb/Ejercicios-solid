package edu.itvo.biblioteca

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SistemaBibliotecaTest {

    private lateinit var biblioteca: Biblioteca
    private lateinit var sistema: SistemaBiblioteca
    private lateinit var usuario: Usuario
    private lateinit var libro1: Libro
    private lateinit var libro2: Libro
    private lateinit var libro3: Libro
    private lateinit var libro4: Libro

    @Before
    fun setUp() {
        biblioteca = Biblioteca()
        sistema = SistemaBiblioteca(biblioteca)

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
        sistema.prestarLibro(libro1, usuario)
        val autorizado = sistema.autorizaPrestamo(usuario.id, libro1.isbn)
        assertFalse(autorizado)
    }

    @Test
    fun usuarioNoPuedeTenerMasDe3LibrosPrestados() {
        sistema.prestarLibro(libro1, usuario)
        sistema.prestarLibro(libro2, usuario)
        sistema.prestarLibro(libro3, usuario)
        val autorizado = sistema.autorizaPrestamo(usuario.id, libro4.isbn)
        assertFalse(autorizado)
    }

    @Test
    fun puedePrestarSiUsuarioTieneMenosDe3LibrosYLibroDisponible() {
        val autorizado = sistema.autorizaPrestamo(usuario.id, libro1.isbn)
        assertTrue(autorizado)
    }

    @Test
    fun devolverLibroLoMarcaComoDisponibleOtraVez() {
        sistema.prestarLibro(libro1, usuario)
        sistema.devolverLibro(libro1, usuario)

        assertTrue(libro1.disponible)
    }

    @Test
    fun mostrarLibrosDisponiblesSoloMuestraNoPrestados() {
        sistema.prestarLibro(libro1, usuario)
        val disponibles = biblioteca.libros.filter { it.disponible }
        assertTrue(disponibles.containsAll(listOf(libro2, libro3, libro4)) && !disponibles.contains(libro1))
    }

    @Test
    fun mostrarLibrosEnPrestamoSoloMuestraPrestados() {
        sistema.prestarLibro(libro2, usuario)
        val enPrestamo = biblioteca.libros.filter { !it.disponible }
        assertTrue(enPrestamo.contains(libro2))
        assertFalse(enPrestamo.contains(libro1))
    }
}