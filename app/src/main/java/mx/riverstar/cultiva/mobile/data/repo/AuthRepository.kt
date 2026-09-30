package mx.riverstar.cultiva.mobile.data.repo

import android.database.sqlite.SQLiteConstraintException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.riverstar.cultiva.mobile.data.db.UsuarioDao
import mx.riverstar.cultiva.mobile.data.db.UsuarioEntity
import mx.riverstar.cultiva.mobile.data.sesion.SesionRepository
import mx.riverstar.cultiva.mobile.dominio.Contrasena

sealed interface ResultadoAuth {
    data object Ok : ResultadoAuth
    data class Error(val mensaje: String) : ResultadoAuth
}

class AuthRepository(
    private val usuarios: UsuarioDao,
    private val sesion: SesionRepository,
) {
    suspend fun registrar(nombre: String, correo: String, contrasena: String): ResultadoAuth {
        val correoNormal = correo.trim().lowercase()
        if (usuarios.porCorreo(correoNormal) != null) {
            return ResultadoAuth.Error("Ya existe una cuenta con ese correo.")
        }
        // PBKDF2 es lento a propósito: se calcula fuera del hilo principal.
        val hash = withContext(Dispatchers.Default) { Contrasena.calcular(contrasena) }
        val id = try {
            usuarios.insertar(
                UsuarioEntity(
                    nombre = nombre.trim(),
                    correo = correoNormal,
                    hashContrasena = hash.hash,
                    sal = hash.sal,
                    creadoEn = System.currentTimeMillis(),
                ),
            )
        } catch (e: SQLiteConstraintException) {
            return ResultadoAuth.Error("Ya existe una cuenta con ese correo.")
        }
        sesion.iniciar(id)
        return ResultadoAuth.Ok
    }

    /**
     * El mensaje de error es el mismo si el correo no existe o si la contraseña
     * es incorrecta: así la pantalla no revela qué correos están registrados.
     */
    suspend fun iniciarSesion(correo: String, contrasena: String): ResultadoAuth {
        val usuario = usuarios.porCorreo(correo.trim().lowercase())
        val valida = usuario != null && withContext(Dispatchers.Default) {
            Contrasena.coincide(contrasena, usuario.hashContrasena, usuario.sal)
        }
        if (usuario == null || !valida) {
            return ResultadoAuth.Error("Correo o contraseña incorrectos.")
        }
        sesion.iniciar(usuario.id)
        return ResultadoAuth.Ok
    }

    suspend fun cerrarSesion() = sesion.cerrar()
}
