package noor.serry.rawaa.ui.screens.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R

@Composable
fun LoginHeader(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .height(263.dp)
            .background(
                AppTheme.color.primary,
                RoundedCornerShape(
                    bottomStart = 32.dp,
                    bottomEnd = 32.dp
                )
            )
            .padding(horizontal = 24.dp)
    ) {
        Text(
            stringResource(R.string.welcome),
            color = AppTheme.color.bg,
            style = AppTheme.textStyle.headline.large,
            modifier = Modifier.padding(top = 64.dp)
        )
        Text(
            stringResource(R.string.login_to_continue),
            color = AppTheme.color.bgSecondary,
            style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}