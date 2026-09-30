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

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorGeneral: String? = null,
    val enviando: Boolean = false,
    val listo: Boolean = false,
)

class LoginViewModel(private val auth: AuthRepository) : ViewModel() {
    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun cambiarCorreo(valor: String) = _estado.update { it.copy(correo = valor, errorCorreo = null, errorGeneral = null) }
    fun cambiarContrasena(valor: String) = _estado.update { it.copy(contrasena = valor, errorContrasena = null, errorGeneral = null) }

    fun entrar() {
        val actual = _estado.value
        if (actual.enviando) return
        val errorCorreo = Validacion.correo(actual.correo)
        val errorContrasena = Validacion.contrasenaLogin(actual.contrasena)
        if (errorCorreo != null || errorContrasena != null) {
            _estado.update { it.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena) }
            return
        }
        _estado.update { it.copy(enviando = true) }
        viewModelScope.launch {
            when (val r = auth.iniciarSesion(actual.correo, actual.contrasena)) {
                ResultadoAuth.Ok -> _estado.update { it.copy(enviando = false, listo = true) }
                is ResultadoAuth.Error -> _estado.update { it.copy(enviando = false, errorGeneral = r.mensaje) }
            }
        }
    }

    companion object {
        val Fabrica = viewModelFactory {
            initializer { LoginViewModel((this[APPLICATION_KEY] as CultivaApp).auth) }
        }
    }
}
