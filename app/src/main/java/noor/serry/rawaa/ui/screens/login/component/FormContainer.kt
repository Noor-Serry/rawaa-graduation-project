package noor.serry.rawaa.ui.screens.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.design.AppTheme

@Composable
fun FormContainer(modifier: Modifier = Modifier,tailContent: @Composable () -> Unit = {},content : @Composable () -> Unit ) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .dropShadow(
                    shape = RoundedCornerShape(24.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = -6.dp,
                        color = AppTheme.color.text.copy(alpha = .1f),
                        offset = DpOffset(x = 0.dp, y = 8.dp)
                    )
                )
                .dropShadow(
                    shape = RoundedCornerShape(24.dp),
                    shadow = Shadow(
                        radius = 25.dp,
                        spread = -5.dp,
                        color = AppTheme.color.text.copy(alpha = .1f),
                        offset = DpOffset(x = 0.dp, y = 20.dp)
                    )
                )
                .background(AppTheme.color.bg, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            content()
        }

        tailContent()
    }
}