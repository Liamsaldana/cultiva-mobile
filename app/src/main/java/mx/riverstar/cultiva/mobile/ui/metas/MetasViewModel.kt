package mx.riverstar.cultiva.mobile.ui.metas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.riverstar.cultiva.mobile.CultivaApp
import mx.riverstar.cultiva.mobile.data.repo.AuthRepository
import mx.riverstar.cultiva.mobile.data.repo.Meta
import mx.riverstar.cultiva.mobile.data.repo.MetaRepository
import mx.riverstar.cultiva.mobile.data.sesion.SesionRepository
import mx.riverstar.cultiva.mobile.dominio.Categoria

data class MetasUiState(
    val cargando: Boolean = true,
    val metas: List<Meta> = emptyList(),
    val hayFiltro: Boolean = false,
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class MetasViewModel(
    private val repo: MetaRepository,
    private val auth: AuthRepository,
    sesion: SesionRepository,
) : ViewModel() {

    /** Lo que la persona escribe. Se muestra tal cual en el TextField, sin retraso. */
    private val _consulta = MutableStateFlow("")
    val consulta: StateFlow<String> = _consulta.asStateFlow()

    /** Chip de categoría elegido; null = todas. */
    private val _categoria = MutableStateFlow<Categoria?>(null)
    val categoria: StateFlow<Categoria?> = _categoria.asStateFlow()

    /** Meta pendiente de confirmar su borrado (abre el diálogo). */
    private val _porBorrar = MutableStateFlow<Meta?>(null)
    val porBorrar: StateFlow<Meta?> = _porBorrar.asStateFlow()

    private val usuarioId = sesion.usuarioId.filterNotNull().distinctUntilChanged()

    /**
     * La lista filtrada. Cada tecla actualiza [_consulta]; el debounce espera a que
     * la persona deje de escribir 250 ms, y flatMapLatest cancela la búsqueda
     * anterior. Room ejecuta la consulta en su propio hilo, así que la interfaz
     * nunca se congela.
     */
    val estado: StateFlow<MetasUiState> =
        combine(usuarioId, _consulta.debounce(250), _categoria) { id, q, cat -> Triple(id, q, cat) }
            .flatMapLatest { (id, q, cat) ->
                repo.buscar(id, q, cat).map { lista ->
                    MetasUiState(cargando = false, metas = lista, hayFiltro = q.isNotBlank() || cat != null)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MetasUiState())

    fun cambiarConsulta(texto: String) { _consulta.value = texto }
    fun elegirCategoria(cat: Categoria?) { _categoria.value = if (_categoria.value == cat) null else cat }
    fun limpiarFiltros() { _consulta.value = ""; _categoria.value = null }

    fun pedirBorrar(meta: Meta) { _porBorrar.value = meta }
    fun cancelarBorrar() { _porBorrar.value = null }

    fun confirmarBorrar() {
        val meta = _porBorrar.value ?: return
        _porBorrar.value = null
        viewModelScope.launch {
            repo.borrar(meta.id, usuarioId.first())
        }
    }

    fun cerrarSesion(despues: () -> Unit) {
        viewModelScope.launch {
            auth.cerrarSesion()
            despues()
        }
    }

    companion object {
        val Fabrica = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CultivaApp
                MetasViewModel(app.metas, app.auth, app.sesion)
            }
        }
    }
}
