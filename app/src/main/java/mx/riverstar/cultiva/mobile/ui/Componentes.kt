package mx.riverstar.cultiva.mobile.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Campo de formulario con su error debajo. El error también se anuncia al lector
 * de pantalla.
 */
@Composable
fun CampoTexto(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    mensajeError: String?,
    modifier: Modifier = Modifier,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accion: ImeAction = ImeAction.Next,
    unaLinea: Boolean = true,
    ayuda: String? = null,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        isError = mensajeError != null,
        supportingText = (mensajeError ?: ayuda)?.let { texto -> { Text(texto) } },
        singleLine = unaLinea,
        minLines = if (unaLinea) 1 else 3,
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado, imeAction = accion),
        modifier = modifier
            .fillMaxWidth()
            .semantics { if (mensajeError != null) error(mensajeError) },
    )
}

@Composable
fun CampoContrasena(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    mensajeError: String?,
    accion: ImeAction = ImeAction.Next,
    ayuda: String? = null,
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        isError = mensajeError != null,
        supportingText = (mensajeError ?: ayuda)?.let { texto -> { Text(texto) } },
        singleLine = true,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = accion),
        trailingIcon = {
            TextButton(onClick = { visible = !visible }) {
                Text(if (visible) "Ocultar" else "Mostrar", style = MaterialTheme.typography.labelMedium)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .semantics { if (mensajeError != null) error(mensajeError) },
    )
}
