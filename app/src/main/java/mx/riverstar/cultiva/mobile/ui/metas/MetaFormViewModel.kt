package mx.riverstar.cultiva.mobile.ui.metas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.riverstar.cultiva.mobile.CultivaApp
import mx.riverstar.cultiva.mobile.data.repo.DatosMeta
import mx.riverstar.cultiva.mobile.data.repo.MetaRepository
import mx.riverstar.cultiva.mobile.data.sesion.SesionRepository
import mx.riverstar.cultiva.mobile.dominio.Categoria
import mx.riverstar.cultiva.mobile.dominio.Dinero
import mx.riverstar.cultiva.mobile.dominio.Validacion
import mx.riverstar.cultiva.mobile.ui.navegacion.Rutas

data class MetaFormUiState(
    val esEdicion: Boolean = false,
    val cargando: Boolean = false,
    val nombre: String = "",
    val categoria: Categoria? = null,
    val montoObjetivo: String = "",
    val montoAhorrado: String = "",
    val nota: String = "",
    val errorNombre: String? = null,
    val errorCategoria: String? = null,
    val errorObjetivo: String? = null,
    val errorAhorrado: String? = null,
    val errorNota: String? = null,
    val errorGeneral: String? = null,
    val guardando: Boolean = false,
    val confirmarBorrado: Boolean = false,
    val terminado: Boolean = false,
)

/**
 * Un solo formulario para CREAR (metaId = null) y ACTUALIZAR (metaId = id).
 * Nada se escribe en la base si algún campo obligatorio está vacío o mal.
 */
class MetaFormViewModel(
    guardado: SavedStateHandle,
    private val repo: MetaRepository,
    private val sesion: SesionRepository,
) : ViewModel() {

    private val metaId: Long? = guardado.get<Long>(Rutas.ARG_ID)?.takeIf { it > 0 }

    private val _estado = MutableStateFlow(MetaFormUiState(esEdicion = metaId != null, cargando = metaId != null))
    val estado: StateFlow<MetaFormUiState> = _estado.asStateFlow()

    init {
        if (metaId != null) cargar(metaId)
    }

    private fun cargar(id: Long) = viewModelScope.launch {
        val usuario = sesion.usuarioId.first() ?: return@launch
        val meta = repo.obtener(id, usuario)
        if (meta == null) {
            _estado.update { it.copy(cargando = false, errorGeneral = "Esta meta ya no existe.") }
            return@launch
        }
        _estado.update {
            it.copy(
                cargando = false,
                nombre = meta.nombre,
                categoria = meta.categoria,
                montoObjetivo = Dinero.aTexto(meta.montoObjetivo),
                montoAhorrado = Dinero.aTexto(meta.montoAhorrado),
                nota = meta.nota,
            )
        }
    }

    fun cambiarNombre(v: String) = _estado.update { it.copy(nombre = v, errorNombre = null) }
    fun cambiarCategoria(v: Categoria) = _estado.update { it.copy(categoria = v, errorCategoria = null) }
    fun cambiarObjetivo(v: String) = _estado.update { it.copy(montoObjetivo = v, errorObjetivo = null) }
    fun cambiarAhorrado(v: String) = _estado.update { it.copy(montoAhorrado = v, errorAhorrado = null) }
    fun cambiarNota(v: String) = _estado.update { it.copy(nota = v, errorNota = null) }

    fun guardar() {
        val a = _estado.value
        if (a.guardando || a.cargando) return
        val conErrores = a.copy(
            errorNombre = Validacion.nombre(a.nombre),
            errorCategoria = Validacion.categoria(a.categoria),
            errorObjetivo = Validacion.montoObjetivo(a.montoObjetivo),
            errorAhorrado = Validacion.montoAhorrado(a.montoAhorrado),
            errorNota = Validacion.nota(a.nota),
        )
        val hayError = listOf(
            conErrores.errorNombre, conErrores.errorCategoria, conErrores.errorObjetivo,
            conErrores.errorAhorrado, conErrores.errorNota,
        ).any { it != null }
        if (hayError) {
            _estado.value = conErrores
            return
        }
        val datos = DatosMeta(
            nombre = a.nombre,
            categoria = a.categoria!!,
            montoObjetivo = Dinero.aCentavos(a.montoObjetivo)!!,
            montoAhorrado = Dinero.aCentavos(a.montoAhorrado) ?: 0L,
            nota = a.nota,
        )
        _estado.update { it.copy(guardando = true, errorGeneral = null) }
        viewModelScope.launch {
            val usuario = sesion.usuarioId.first()
            val ok = when {
                usuario == null -> false
                metaId == null -> { repo.crear(usuario, datos); true }
                else -> repo.actualizar(metaId, usuario, datos)
            }
            _estado.update {
                if (ok) it.copy(guardando = false, terminado = true)
                else it.copy(guardando = false, errorGeneral = "No se pudo guardar. Intenta de nuevo.")
            }
        }
    }

    fun pedirBorrar() = _estado.update { it.copy(confirmarBorrado = true) }
    fun cancelarBorrar() = _estado.update { it.copy(confirmarBorrado = false) }

    fun borrar() {
        val id = metaId ?: return
        _estado.update { it.copy(confirmarBorrado = false, guardando = true) }
        viewModelScope.launch {
            val usuario = sesion.usuarioId.first()
            val ok = usuario != null && repo.borrar(id, usuario)
            _estado.update {
                if (ok) it.copy(guardando = false, terminado = true)
                else it.copy(guardando = false, errorGeneral = "No se pudo borrar.")
            }
        }
    }

    companion object {
        val Fabrica = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CultivaApp
                MetaFormViewModel(createSavedStateHandle(), app.metas, app.sesion)
            }
        }
    }
}
