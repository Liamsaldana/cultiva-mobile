package mx.riverstar.cultiva.mobile.dominio

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContrasenaTest {
    @Test fun laCorrectaCoincide() {
        val h = Contrasena.calcular("cultiva2026")
        assertTrue(Contrasena.coincide("cultiva2026", h.hash, h.sal))
    }

    @Test fun unaIncorrectaNoCoincide() {
        val h = Contrasena.calcular("cultiva2026")
        assertFalse(Contrasena.coincide("cultiva2027", h.hash, h.sal))
    }

    @Test fun noSeGuardaEnClaro() {
        val h = Contrasena.calcular("cultiva2026")
        assertFalse(h.hash.contains("cultiva2026"))
    }

    @Test fun mismaContrasenaDistintaSalDaDistintoHash() {
        assertNotEquals(Contrasena.calcular("cultiva2026").hash, Contrasena.calcular("cultiva2026").hash)
    }
}
