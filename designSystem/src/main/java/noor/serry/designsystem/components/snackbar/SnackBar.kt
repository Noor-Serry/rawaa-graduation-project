package noor.serry.designsystem.components.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.R
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme

@Composable
fun SnackBar(
    snackBarUiMessage: SnackBarUiMessage
) {
    val mark = if (snackBarUiMessage.isSuccess) R.drawable.correct else R.drawable.exclamation_mark
    val color = if (snackBarUiMessage.isSuccess) AppTheme.color.secondary else AppTheme.color.error

    // Background and border adapt to success/error state
    val bgColor = if (snackBarUiMessage.isSuccess) AppTheme.color.successBg else AppTheme.color.errorBg
    val borderColor = if (snackBarUiMessage.isSuccess) AppTheme.color.successBorder else AppTheme.color.errorBorder

    // Icon tint: use a light color visible on the colored circle
    val iconTint = AppTheme.color.bg // white (#FFFFFF) from your scheme

    Row(
        modifier = snackBarUiMessage.modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(48.dp))
            .border(1.dp, borderColor, RoundedCornerShape(48.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(color, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(mark),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(10.dp)
            )
        }

        Text(
            text = snackBarUiMessage.messageRes,
            style = AppTheme.textStyle.label.medium,   // 12sp / Normal — compact snackbar text
            color = AppTheme.color.text,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)             // pushes action text to the end
        )

        snackBarUiMessage.actionText?.let {
            Text(
                text = it,
                style = AppTheme.textStyle.label.large, // 14sp / SemiBold
                color = color,
                modifier = Modifier.clickAnimation(onClick = snackBarUiMessage.onActionClick ?: {})
            )
        }
    }
}

data class SnackBarUiMessage(
    val isSuccess: Boolean,
    val messageRes: String,
    val modifier: Modifier = Modifier,
    val actionText: String? = null,
    val onActionClick: (() -> Unit)? = null
)