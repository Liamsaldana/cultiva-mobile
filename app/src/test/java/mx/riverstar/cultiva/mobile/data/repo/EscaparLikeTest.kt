package mx.riverstar.cultiva.mobile.data.repo

import org.junit.Assert.assertEquals
import org.junit.Test

class EscaparLikeTest {
    @Test fun porcentajeSeBuscaLiteral() = assertEquals("50\\%", MetaRepository.escaparLike("50%"))
    @Test fun guionBajoSeBuscaLiteral() = assertEquals("a\\_b", MetaRepository.escaparLike("a_b"))
    @Test fun textoNormalNoCambia() = assertEquals("viaje", MetaRepository.escaparLike("viaje"))
}
