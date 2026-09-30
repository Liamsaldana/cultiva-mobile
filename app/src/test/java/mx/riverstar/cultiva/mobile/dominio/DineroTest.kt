package mx.riverstar.cultiva.mobile.dominio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DineroTest {
    @Test fun enteros() = assertEquals(150000L, Dinero.aCentavos("1500"))
    @Test fun conComasYSigno() = assertEquals(150050L, Dinero.aCentavos("$1,500.50"))
    @Test fun unDecimal() = assertEquals(1250L, Dinero.aCentavos("12.5"))
    @Test fun vacioEsNull() = assertNull(Dinero.aCentavos("   "))
    @Test fun textoEsNull() = assertNull(Dinero.aCentavos("mil"))
    @Test fun tresDecimalesEsNull() = assertNull(Dinero.aCentavos("1.999"))
    @Test fun negativoEsNull() = assertNull(Dinero.aCentavos("-50"))
    @Test fun idaYVuelta() = assertEquals(150050L, Dinero.aCentavos(Dinero.aTexto(150050)))
    @Test fun aTextoConCentavos() = assertEquals("1500.05", Dinero.aTexto(150005))
    @Test fun aTextoSinCentavos() = assertEquals("80", Dinero.aTexto(8000))
}
