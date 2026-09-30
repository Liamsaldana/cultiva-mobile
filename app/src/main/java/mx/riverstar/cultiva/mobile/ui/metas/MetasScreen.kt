package mx.riverstar.cultiva.mobile.ui.metas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.riverstar.cultiva.mobile.data.repo.Meta
import mx.riverstar.cultiva.mobile.dominio.Categoria
import mx.riverstar.cultiva.mobile.dominio.Dinero

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetasScreen(
    alCrear: () -> Unit,
    alEditar: (Long) -> Unit,
    alCerrarSesion: () -> Unit,
    vm: MetasViewModel = viewModel(factory = MetasViewModel.Fabrica),
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val consulta by vm.consulta.collectAsStateWithLifecycle()
    val categoria by vm.categoria.collectAsStateWithLifecycle()
    val porBorrar by vm.porBorrar.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis metas", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { vm.cerrarSesion(alCerrarSesion) }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = alCrear,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nueva meta") },
            )
        },
    ) { relleno ->
        Column(Modifier.padding(relleno).fillMaxSize()) {
            // --- Buscador en tiempo real ---
            OutlinedTextField(
                value = consulta,
                onValueChange = vm::cambiarConsulta,
                placeholder = { Text("Buscar por nombre o nota") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (consulta.isNotEmpty()) {
                        IconButton(onClick = { vm.cambiarConsulta("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Borrar búsqueda")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )

            // --- Filtro por categoría (chips de selección rápida) ---
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    FilterChip(
                        selected = categoria == null,
                        onClick = { vm.elegirCategoria(null) },
                        label = { Text("Todas") },
                    )
                }
                items(Categoria.entries) { cat ->
                    FilterChip(
                        selected = categoria == cat,
                        onClick = { vm.elegirCategoria(cat) },
                        label = { Text(cat.etiqueta) },
                    )
                }
            }

            when {
                estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                estado.metas.isEmpty() -> ListaVacia(hayFiltro = estado.hayFiltro, alLimpiar = vm::limpiarFiltros)
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(estado.metas, key = { it.id }) { meta ->
                        TarjetaMeta(
                            meta = meta,
                            alTocar = { alEditar(meta.id) },
                            alBorrar = { vm.pedirBorrar(meta) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }

    // --- Confirmación antes de borrar ---
    porBorrar?.let { meta ->
        AlertDialog(
            onDismissRequest = vm::cancelarBorrar,
            title = { Text("¿Borrar esta meta?") },
            text = {
                Text("Se borrará «${meta.nombre}» con ${Dinero.formatear(meta.montoAhorrado)} registrados. No se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = vm::confirmarBorrar) {
                    Text("Borrar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = vm::cancelarBorrar) { Text("Cancelar") }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaMeta(meta: Meta, alTocar: () -> Unit, alBorrar: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = alTocar,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 16.dp, end = 4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        meta.categoria.etiqueta.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        meta.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = alBorrar) {
                    Icon(Icons.Filled.Delete, contentDescription = "Borrar ${meta.nombre}", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { meta.avance },
                modifier = Modifier.fillMaxWidth().padding(end = 12.dp).height(8.dp),
                trackColor = MaterialTheme.colorScheme.background,
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.padding(end = 12.dp)) {
                Text(
                    "${Dinero.formatear(meta.montoAhorrado)} de ${Dinero.formatear(meta.montoObjetivo)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "${(meta.avance * 100).toInt()} %",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (meta.nota.isNotBlank()) {
                Text(
                    meta.nota,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp, end = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun ListaVacia(hayFiltro: Boolean, alLimpiar: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (hayFiltro) {
            Text("Nada coincide con tu búsqueda.", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = alLimpiar) { Text("Quitar filtros") }
        } else {
            Text("Todavía no tienes metas.", style = MaterialTheme.typography.titleMedium)
            Text(
                "Crea la primera con «Nueva meta».",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
