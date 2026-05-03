package noor.serry.rawaa.ui.screens.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R

@Composable
fun LoginFooter(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.by_using_rawaa_you_agree),
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
        )
        Row(modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.privacy_polices),
                color = AppTheme.color.primary,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
            )
            Text(
                text = "•",
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
            )
            Text(
                text = stringResource(R.string.usage_conditions),
                color = AppTheme.color.primary,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
            )
        }
    }
}