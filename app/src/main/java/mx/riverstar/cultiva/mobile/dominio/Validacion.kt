package mx.riverstar.cultiva.mobile.dominio

/**
 * Reglas de validación puras (sin Android), para poder probarlas con JUnit.
 * Cada función devuelve el mensaje de error, o null si el campo es válido.
 */
object Validacion {
    private val CORREO = Regex("""^[^\s@]+@[^\s@]+\.[^\s@]{2,}$""")

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "Escribe un nombre."
        valor.trim().length > 60 -> "Máximo 60 caracteres."
        else -> null
    }

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "Escribe tu correo."
        !CORREO.matches(valor.trim()) -> "Ese correo no parece válido."
        else -> null
    }

    fun contrasenaNueva(valor: String): String? = when {
        valor.isEmpty() -> "Escribe una contraseña."
        valor.length < 8 -> "Al menos 8 caracteres."
        !valor.any { it.isDigit() } || !valor.any { it.isLetter() } -> "Usa letras y al menos un número."
        else -> null
    }

    fun contrasenaLogin(valor: String): String? =
        if (valor.isEmpty()) "Escribe tu contraseña." else null

    fun montoObjetivo(texto: String): String? {
        if (texto.isBlank()) return "Escribe cuánto quieres juntar."
        val centavos = Dinero.aCentavos(texto) ?: return "Escribe un monto válido, por ejemplo 1500 o 1500.50."
        return if (centavos <= 0) "El monto tiene que ser mayor a cero." else null
    }

    fun montoAhorrado(texto: String): String? {
        if (texto.isBlank()) return null // opcional: vacío cuenta como $0
        return if (Dinero.aCentavos(texto) == null) "Escribe un monto válido." else null
    }

    fun categoria(valor: Categoria?): String? = if (valor == null) "Elige una categoría." else null

    fun nota(valor: String): String? = if (valor.length > 200) "Máximo 200 caracteres." else null
}
