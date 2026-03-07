package noor.serry.rawaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            false
        }
        setContent {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(
                    textAlign =
                        TextAlign.Center, text = "bgjfb", color = Color(0xFFFFFFFF)
                )
            }
        }
    }
}
