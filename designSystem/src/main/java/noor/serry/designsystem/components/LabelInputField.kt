package noor.serry.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import noor.serry.designsystem.design.AppTheme

@Composable
fun LabelInputField(
    label: String,
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String = "",
    isPassword: Boolean = false,
    maxLines: Int = 1,
    isError: Boolean = false,
    errorMessage: String? = null,
    height: Dp = Dp.Unspecified,
    isEnabled: Boolean = true,
    maxCharacters: Int = Int.MAX_VALUE,
    borderColor: Color = AppTheme.color.border,
    cursorBrushColor: Color = AppTheme.color.primary,
    borderFocusedColor: Color = AppTheme.color.borderFocus,
    containerColor: Color = AppTheme.color.bg,
    hintColor: Color = AppTheme.color.textSecondary,
    icon: Painter? = null,
    isArabic: Boolean = true,
    textColor: Color = AppTheme.color.text,
    degree: Int = 12,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    labelStyle : TextStyle = AppTheme.textStyle.body.small,
    inputFieldStyle : TextStyle = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Normal)
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = AppTheme.color.primaryDark,
            style = labelStyle
        )

        BaseInputField(
            text = text,
            onValueChange = onValueChange,
            modifier = Modifier,
            style = inputFieldStyle,
            hintText = hintText,
            isPassword = isPassword,
            maxLines = maxLines,
            height = height,
            isEnabled = isEnabled,
            maxCharacters = maxCharacters,
            borderColor = if (isError) AppTheme.color.error else borderColor,
            cursorBrushColor = cursorBrushColor,
            borderFocusedColor = if (isError) AppTheme.color.error else borderFocusedColor,
            containerColor = containerColor,
            hintColor = hintColor,
            icon = icon,
            isArabic = isArabic,
            textColor = textColor,
            degree = degree,
            keyboardOptions = keyboardOptions
        )

        AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Text(
                text = errorMessage.orEmpty(),
                color = AppTheme.color.error,
                style = AppTheme.textStyle.body.small,
            )
        }
    }
}