package noor.serry.rawaa.ui.screens.home_student.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme

@Composable
fun SectionHeader(
    title: String,
    actionText: String = "عرض الكل",
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = AppTheme.color.text,
            style = AppTheme.textStyle.headline.small.copy(fontSize = 18.sp),
        )
        Text(
            text = actionText,
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.body.small,
            modifier = Modifier.clickAnimation { onActionClick() }
        )
    }
}
