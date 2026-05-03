package noor.serry.designsystem.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.R
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme


@Composable
fun BaseInputField(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = AppTheme.textStyle.label.medium,
    hintText: String = "",
    isPassword : Boolean = false,
    maxLines: Int = 1,
    height: Dp = Dp.Unspecified,
    isEnabled: Boolean = true,
    maxCharacters: Int = Int.MAX_VALUE,
    borderColor: Color = AppTheme.color.border,
    cursorBrushColor: Color = AppTheme.color.primary,
    borderFocusedColor: Color = AppTheme.color.borderFocus,
    containerColor : Color = AppTheme.color.bg,
    hintColor: Color = AppTheme.color.textSecondary,
    icon: Painter? = null,
    isArabic: Boolean = true,
    textColor : Color = AppTheme.color.text,
    degree : Int = 12,
    keyboardOptions: KeyboardOptions = KeyboardOptions()
) {
    var isFocused by remember { mutableStateOf(false) }

    var isPasswordHidden by remember { mutableStateOf(true) }

    val currentBorderColor by animateColorAsState(
        targetValue = if (isFocused) borderFocusedColor else borderColor
    )


    CompositionLocalProvider(
        LocalLayoutDirection provides if (isArabic) LayoutDirection.Rtl
        else LayoutDirection.Ltr
    ) {
            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = currentBorderColor,
                        shape = RoundedCornerShape(degree.dp)
                    )
                    .defaultMinSize(minHeight = 56.dp).background(containerColor,RoundedCornerShape(degree.dp))
                    .height(height).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                icon?.let {
                    Image(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(currentBorderColor)
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = {
                        if (it.length <= maxCharacters) {
                            onValueChange(it)
                        } else if (it.length > text.length + 1) {
                            onValueChange(it.substring(0, maxCharacters))
                        }
                    },
                    maxLines = maxLines,
                    enabled = isEnabled,
                    modifier = modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 50.dp)
                        .height(height)
                        .onFocusChanged { focusState -> isFocused = focusState.isFocused },
                    keyboardOptions = keyboardOptions,
                    textStyle = style.copy(color = textColor),
                    cursorBrush = SolidColor(cursorBrushColor),
                    visualTransformation = if (isPassword && isPasswordHidden) PasswordVisualTransformation('*') else VisualTransformation.None,
                    singleLine = maxLines == 1,
                    decorationBox = { innerTextField ->
                        InnerTextFieldWithHint(innerTextField, text, hintText, maxLines, hintColor,style)
                    }
                )
                if (isPassword)
                AnimatedContent(isPasswordHidden) {
                    if (it)
                            Icon(
                                painter = painterResource(R.drawable.password_eye_close),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp).clickAnimation(onClick = {
                                    isPasswordHidden = false
                                }),
                                tint = currentBorderColor
                            )
                    else
                        Icon(
                            painter = painterResource(R.drawable.password_eye_open),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp).clickAnimation(onClick = {
                                isPasswordHidden = true
                            }),
                            tint = currentBorderColor
                        )
                }
            }

    }
}

@Composable
private fun InnerTextFieldWithHint(
    innerTextField: @Composable (() -> Unit),
    text: String,
    hintText: String,
    maxLines: Int,
    hintColor: Color ,
    style: TextStyle
) {
    Box(
        modifier = if (maxLines == 1) Modifier else Modifier
            .padding(vertical = 5.dp)
            .padding(top = (if (LocalLayoutDirection.current == LayoutDirection.Rtl) 0 else 3).dp),
        contentAlignment = if (maxLines == 1) Alignment.CenterStart else Alignment.TopStart,
    ) {
        innerTextField()
        if (text.isEmpty()) {
            BasicText(
                text = hintText,
                modifier = Modifier,
                style = style.copy(color = hintColor)
            )
        }
    }
}