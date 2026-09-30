package mx.riverstar.cultiva.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Claro = lightColorScheme(
    primary = VidaOsc,
    onPrimary = Papel,
    primaryContainer = VidaClaro,
    onPrimaryContainer = Tinta,
    secondary = Vino,
    onSecondary = Papel,
    background = Papel,
    onBackground = Tinta,
    surface = Papel,
    onSurface = Tinta,
    surfaceVariant = Papel2,
    onSurfaceVariant = TintaSuave,
    surfaceContainer = Papel2,
    surfaceContainerHigh = Papel2,
    outline = TintaSuave,
    error = Alerta,
    onError = Papel,
)

private val Oscuro = darkColorScheme(
    primary = VidaClaro,
    onPrimary = JadeFondo,
    primaryContainer = VidaOsc,
    onPrimaryContainer = JadeTinta,
    secondary = Color(0xFFE58AA6),
    onSecondary = JadeFondo,
    background = JadePapel,
    onBackground = JadeTinta,
    surface = JadePapel,
    onSurface = JadeTinta,
    surfaceVariant = JadePapel2,
    onSurfaceVariant = JadeTintaSuave,
    surfaceContainer = JadePapel2,
    surfaceContainerHigh = JadePapel2,
    outline = JadeTintaSuave,
    error = JadeAlerta,
    onError = JadeFondo,
)

private val Tipografia = Typography(
    headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    // Piso de letra de Cultiva: nada de producto por debajo de 12 sp.
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
)

@Composable
fun CultivaTheme(oscuro: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (oscuro) Oscuro else Claro,
        typography = Tipografia,
        content = content,
    )
}
