package mx.riverstar.cultiva.mobile.dominio

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * La contraseña NUNCA se guarda: se guarda un hash PBKDF2 con una sal aleatoria
 * por usuario. Para iniciar sesión se recalcula el hash y se compara en tiempo
 * constante.
 */
object Contrasena {
    private const val ITERACIONES = 120_000
    private const val LONGITUD_BITS = 256
    private const val ALGORITMO = "PBKDF2WithHmacSHA256"

    data class Hash(val hash: String, val sal: String)

    fun nuevaSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun calcular(contrasena: String, sal: String = nuevaSal()): Hash {
        val spec = PBEKeySpec(contrasena.toCharArray(), Base64.getDecoder().decode(sal), ITERACIONES, LONGITUD_BITS)
        try {
            val bytes = SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).encoded
            return Hash(Base64.getEncoder().encodeToString(bytes), sal)
        } finally {
            spec.clearPassword()
        }
    }

    fun coincide(contrasena: String, hashGuardado: String, sal: String): Boolean {
        val calculado = calcular(contrasena, sal).hash
        return MessageDigest.isEqual(calculado.toByteArray(), hashGuardado.toByteArray())
    }
}
