package mx.riverstar.cultiva.mobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.riverstar.cultiva.mobile.CultivaApp
import mx.riverstar.cultiva.mobile.data.repo.AuthRepository
import mx.riverstar.cultiva.mobile.data.repo.ResultadoAuth
import mx.riverstar.cultiva.mobile.dominio.Validacion

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmacion: String? = null,
    val errorGeneral: String? = null,
    val enviando: Boolean = false,
    val listo: Boolean = false,
)

class RegistroViewModel(private val auth: AuthRepository) : ViewModel() {
    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    fun cambiarNombre(v: String) = _estado.update { it.copy(nombre = v, errorNombre = null, errorGeneral = null) }
    fun cambiarCorreo(v: String) = _estado.update { it.copy(correo = v, errorCorreo = null, errorGeneral = null) }
    fun cambiarContrasena(v: String) = _estado.update { it.copy(contrasena = v, errorContrasena = null) }
    fun cambiarConfirmacion(v: String) = _estado.update { it.copy(confirmacion = v, errorConfirmacion = null) }

    fun crearCuenta() {
        val a = _estado.value
        if (a.enviando) return
        val errores = a.copy(
            errorNombre = Validacion.nombre(a.nombre),
            errorCorreo = Validacion.correo(a.correo),
            errorContrasena = Validacion.contrasenaNueva(a.contrasena),
            errorConfirmacion = if (a.confirmacion != a.contrasena) "Las contraseñas no coinciden." else null,
        )
        if (listOf(errores.errorNombre, errores.errorCorreo, errores.errorContrasena, errores.errorConfirmacion).any { it != null }) {
            _estado.value = errores
            return
        }
        _estado.update { it.copy(enviando = true) }
        viewModelScope.launch {
            when (val r = auth.registrar(a.nombre, a.correo, a.contrasena)) {
                ResultadoAuth.Ok -> _estado.update { it.copy(enviando = false, listo = true) }
                is ResultadoAuth.Error -> _estado.update { it.copy(enviando = false, errorGeneral = r.mensaje) }
            }
        }
    }

    companion object {
        val Fabrica = viewModelFactory {
            initializer { RegistroViewModel((this[APPLICATION_KEY] as CultivaApp).auth) }
        }
    }
}
