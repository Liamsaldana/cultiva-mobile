package mx.riverstar.cultiva.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import mx.riverstar.cultiva.mobile.ui.navegacion.CultivaNavHost
import mx.riverstar.cultiva.mobile.ui.theme.CultivaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CultivaTheme {
                CultivaNavHost()
            }
        }
    }
}
