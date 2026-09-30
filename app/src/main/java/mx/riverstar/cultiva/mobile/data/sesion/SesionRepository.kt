package mx.riverstar.cultiva.mobile.data.sesion

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sesionStore by preferencesDataStore(name = "sesion")

/**
 * Sesión básica: sólo se recuerda el id del usuario que inició sesión.
 * Sobrevive a cerrar la app; se borra al cerrar sesión.
 */
class SesionRepository(private val context: Context) {
    private val claveUsuario = longPreferencesKey("usuario_id")

    /** null = no hay sesión. */
    val usuarioId: Flow<Long?> = context.sesionStore.data.map { it[claveUsuario] }

    suspend fun iniciar(usuarioId: Long) {
        context.sesionStore.edit { it[claveUsuario] = usuarioId }
    }

    suspend fun cerrar() {
        context.sesionStore.edit { it.remove(claveUsuario) }
    }
}
