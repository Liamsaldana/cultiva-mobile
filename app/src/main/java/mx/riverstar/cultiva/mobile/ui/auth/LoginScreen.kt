package mx.riverstar.cultiva.mobile.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.riverstar.cultiva.mobile.R
import mx.riverstar.cultiva.mobile.ui.CampoContrasena
import mx.riverstar.cultiva.mobile.ui.CampoTexto

@Composable
fun LoginScreen(
    alEntrar: () -> Unit,
    alCrearCuenta: () -> Unit,
    vm: LoginViewModel = viewModel(factory = LoginViewModel.Fabrica),
) {
    val estado by vm.estado.collectAsStateWithLifecycle()

    // Cuando el login termina bien, la navegación saca el Login de la pila.
    LaunchedEffect(estado.listo) { if (estado.listo) alEntrar() }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(painterResource(R.drawable.ic_cultiva), contentDescription = null, modifier = Modifier.size(88.dp))
            Text("Cultiva", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Tu dinero no es un examen. Entra para ver tus metas.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))

            CampoTexto(
                valor = estado.correo,
                alCambiar = vm::cambiarCorreo,
                etiqueta = "Correo",
                mensajeError = estado.errorCorreo,
                tipoTeclado = KeyboardType.Email,
            )
            CampoContrasena(
                valor = estado.contrasena,
                alCambiar = vm::cambiarContrasena,
                etiqueta = "Contraseña",
                mensajeError = estado.errorContrasena,
                accion = ImeAction.Done,
            )
            estado.errorGeneral?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            Button(
                onClick = vm::entrar,
                enabled = !estado.enviando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                if (estado.enviando) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text("Entrar")
                }
            }
            TextButton(onClick = alCrearCuenta) { Text("¿No tienes cuenta? Crea una") }
        }
    }
}
