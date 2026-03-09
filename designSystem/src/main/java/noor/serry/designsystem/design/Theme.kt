package noor.serry.designsystem.design

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun RawaaTheme(
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalAppColors provides darkThemeColors,
    ) {
            content()
    }
}

object AppTheme {
    val color: AppColorScheme
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current

    val textStyle: AppTypography
        @Composable @ReadOnlyComposable
        get() = LocalTypography.current
}