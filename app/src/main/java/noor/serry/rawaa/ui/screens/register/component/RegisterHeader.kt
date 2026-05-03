package noor.serry.rawaa.ui.screens.register.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R

@Composable
fun RegisterHeader(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    currentStep : Int
) {
    Column(
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 272.dp)
            .background(
                AppTheme.color.primary
            )
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(painter = painterResource(R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = AppTheme.color.bg,
                modifier = Modifier.size(20.dp).clickAnimation { onBackClick() }
            )
            Text(
                text = stringResource(R.string.back),
                color = AppTheme.color.bg,
                style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.clickAnimation { onBackClick() }
            )
        }

        Text(
            stringResource(R.string.create_new_account),
            color = AppTheme.color.bg,
            style = AppTheme.textStyle.headline.large,
            modifier = Modifier.padding(top = 24.dp)
        )
        Text(
            stringResource(R.string.join_rawaa_academic_community),
            color = AppTheme.color.bgSecondary,
            style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(top = 8.dp)
        )
        StepProgressIndicator(
            totalSteps = 3,
            currentStep = currentStep,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}
