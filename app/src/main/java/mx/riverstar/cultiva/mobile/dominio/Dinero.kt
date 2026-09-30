package mx.riverstar.cultiva.mobile.dominio

import java.text.NumberFormat
import java.util.Locale

/**
 * Los montos se guardan en CENTAVOS enteros (Long), nunca en coma flotante:
 * así sumar y comparar dinero es exacto.
 */
object Dinero {
    private val formato: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    /** 150050 -> "$1,500.50" */
    fun formatear(centavos: Long): String = formato.format(centavos / 100.0)

    /**
     * Convierte lo que escribe la persona ("1,500.50", "$800", "12") a centavos.
     * Devuelve null si el texto no es un monto válido. Acepta hasta dos decimales.
     */
    fun aCentavos(texto: String): Long? {
        val limpio = texto.trim().removePrefix("$").replace(",", "").trim()
        if (limpio.isEmpty()) return null
        if (!Regex("""\d{1,12}(\.\d{1,2})?""").matches(limpio)) return null
        val partes = limpio.split(".")
        val enteros = partes[0].toLong()
        val decimales = if (partes.size == 2) partes[1].padEnd(2, '0').toLong() else 0L
        return enteros * 100 + decimales
    }

    /** 150050 -> "1500.50", para rellenar el formulario al editar. */
    fun aTexto(centavos: Long): String {
        val enteros = centavos / 100
        val resto = centavos % 100
        return if (resto == 0L) enteros.toString() else "$enteros.${resto.toString().padStart(2, '0')}"
    }
}
