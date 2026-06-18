package noor.serry.rawaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel
import noor.serry.designsystem.design.RawaaTheme
import noor.serry.rawaa.ui.MainUiState
import noor.serry.rawaa.ui.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    val config = ClarityConfig(
        projectId = "wqjdlv9iae",
        logLevel = LogLevel.None
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            mainViewModel.uiState.value == MainUiState.Loading
        }

        Clarity.initialize(applicationContext, config)

        setContent {
            RawaaTheme {
                val activity = LocalActivity.current
                val view = LocalView.current
                WindowCompat.getInsetsController(activity!!.window, view)
                    .isAppearanceLightStatusBars = true

                RawaaApp(mainViewModel = mainViewModel)
            }
        }
    }
}