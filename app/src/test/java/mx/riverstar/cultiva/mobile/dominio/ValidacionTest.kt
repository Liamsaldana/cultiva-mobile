package mx.riverstar.cultiva.mobile.dominio

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/** El formulario no debe dejar pasar campos obligatorios vacíos. */
class ValidacionTest {
    @Test fun nombreVacioFalla() = assertNotNull(Validacion.nombre("   "))
    @Test fun nombreValido() = assertNull(Validacion.nombre("Fondo de emergencia"))
    @Test fun nombreLargoFalla() = assertNotNull(Validacion.nombre("x".repeat(61)))

    @Test fun correoVacioFalla() = assertNotNull(Validacion.correo(""))
    @Test fun correoSinArrobaFalla() = assertNotNull(Validacion.correo("alex.cultiva.mx"))
    @Test fun correoValido() = assertNull(Validacion.correo("persona@ejemplo.com"))

    @Test fun contrasenaCortaFalla() = assertNotNull(Validacion.contrasenaNueva("abc12"))
    @Test fun contrasenaSinNumeroFalla() = assertNotNull(Validacion.contrasenaNueva("sinnumeros"))
    @Test fun contrasenaValida() = assertNull(Validacion.contrasenaNueva("cultiva2026"))

    @Test fun objetivoVacioFalla() = assertNotNull(Validacion.montoObjetivo(""))
    @Test fun objetivoCeroFalla() = assertNotNull(Validacion.montoObjetivo("0"))
    @Test fun objetivoTextoFalla() = assertNotNull(Validacion.montoObjetivo("mucho"))
    @Test fun objetivoValido() = assertNull(Validacion.montoObjetivo("8,000"))

    @Test fun ahorradoVacioEsValido() = assertNull(Validacion.montoAhorrado(""))
    @Test fun ahorradoTextoFalla() = assertNotNull(Validacion.montoAhorrado("abc"))

    @Test fun sinCategoriaFalla() = assertNotNull(Validacion.categoria(null))
    @Test fun conCategoria() = assertNull(Validacion.categoria(Categoria.AHORRO))
}
