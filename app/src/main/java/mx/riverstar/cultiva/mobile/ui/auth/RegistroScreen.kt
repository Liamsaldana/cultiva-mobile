package mx.riverstar.cultiva.mobile.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.riverstar.cultiva.mobile.ui.CampoContrasena
import mx.riverstar.cultiva.mobile.ui.CampoTexto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    alRegistrar: () -> Unit,
    alVolver: () -> Unit,
    vm: RegistroViewModel = viewModel(factory = RegistroViewModel.Fabrica),
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    LaunchedEffect(estado.listo) { if (estado.listo) alRegistrar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { relleno ->
        Column(
            modifier = Modifier
                .padding(relleno)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Tu cuenta vive sólo en este teléfono.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            CampoTexto(estado.nombre, vm::cambiarNombre, "Nombre", estado.errorNombre)
            CampoTexto(estado.correo, vm::cambiarCorreo, "Correo", estado.errorCorreo, tipoTeclado = KeyboardType.Email)
            CampoContrasena(
                estado.contrasena, vm::cambiarContrasena, "Contraseña", estado.errorContrasena,
                ayuda = "Mínimo 8 caracteres, con letras y un número.",
            )
            CampoContrasena(
                estado.confirmacion, vm::cambiarConfirmacion, "Repite la contraseña", estado.errorConfirmacion,
                accion = ImeAction.Done,
            )
            estado.errorGeneral?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            Button(
                onClick = vm::crearCuenta,
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                if (estado.enviando) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Crear cuenta")
                }
            }
        }
    }
}
