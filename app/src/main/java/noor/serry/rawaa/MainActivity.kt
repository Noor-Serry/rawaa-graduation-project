package noor.serry.rawaa

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import noor.serry.designsystem.design.AppTheme
import noor.serry.designsystem.design.RawaaTheme
import noor.serry.rawaa.ui.screens.onboarding.OnboardingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            false
        }
        setContent {
            RawaaTheme {
                val activity = LocalActivity.current
                val view = LocalView.current

                if (activity != null) {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O){
                        activity.window.navigationBarColor = AppTheme.color.bg.toArgb()
                    }
                    WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = true
                }
                RawaaApp()
            }
        }
    }
}
