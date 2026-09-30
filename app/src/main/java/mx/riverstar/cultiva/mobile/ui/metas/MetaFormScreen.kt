package mx.riverstar.cultiva.mobile.ui.metas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.riverstar.cultiva.mobile.dominio.Categoria
import mx.riverstar.cultiva.mobile.ui.CampoTexto

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MetaFormScreen(
    alTerminar: () -> Unit,
    vm: MetaFormViewModel = viewModel(factory = MetaFormViewModel.Fabrica),
) {
    val e by vm.estado.collectAsStateWithLifecycle()
    LaunchedEffect(e.terminado) { if (e.terminado) alTerminar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (e.esEdicion) "Editar meta" else "Nueva meta") },
                navigationIcon = {
                    IconButton(onClick = alTerminar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver sin guardar")
                    }
                },
                actions = {
                    if (e.esEdicion && !e.cargando) {
                        IconButton(onClick = vm::pedirBorrar) {
                            Icon(Icons.Filled.Delete, contentDescription = "Borrar meta")
                        }
                    }
                },
            )
        },
    ) { relleno ->
        if (e.cargando) {
            Box(Modifier.padding(relleno).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(relleno)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CampoTexto(e.nombre, vm::cambiarNombre, "Nombre de la meta *", e.errorNombre)

            Text("Categoría *", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Categoria.entries.forEach { cat ->
                    FilterChip(
                        selected = e.categoria == cat,
                        onClick = { vm.cambiarCategoria(cat) },
                        label = { Text(cat.etiqueta) },
                    )
                }
            }
            e.errorCategoria?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
            }

            CampoTexto(
                e.montoObjetivo, vm::cambiarObjetivo, "¿Cuánto quieres juntar? *", e.errorObjetivo,
                tipoTeclado = KeyboardType.Decimal, ayuda = "En pesos. Ejemplo: 8000",
            )
            CampoTexto(
                e.montoAhorrado, vm::cambiarAhorrado, "¿Cuánto llevas?", e.errorAhorrado,
                tipoTeclado = KeyboardType.Decimal, ayuda = "Opcional. Si lo dejas vacío, cuenta como $0",
            )
            CampoTexto(
                e.nota, vm::cambiarNota, "Nota", e.errorNota,
                unaLinea = false, accion = ImeAction.Default, ayuda = "Opcional",
            )

            e.errorGeneral?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                "* Campo obligatorio",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = vm::guardar,
                enabled = !e.guardando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                if (e.guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (e.esEdicion) "Guardar cambios" else "Crear meta")
                }
            }
        }
    }

    if (e.confirmarBorrado) {
        AlertDialog(
            onDismissRequest = vm::cancelarBorrar,
            title = { Text("¿Borrar esta meta?") },
            text = { Text("Se borrará «${e.nombre}». No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = vm::borrar) { Text("Borrar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = vm::cancelarBorrar) { Text("Cancelar") } },
        )
    }
}
