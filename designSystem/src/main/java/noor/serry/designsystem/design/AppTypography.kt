package noor.serry.designsystem.design

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noor.serry.designsystem.R

data class AppTypography(
    val headline: SizedTextStyle,
    val body: SizedTextStyle,
    val label: SizedTextStyle
)

data class SizedTextStyle(
    val large: TextStyle,
    val medium: TextStyle,
    val small: TextStyle,
)

val cairo = FontFamily(
    Font(R.font.cairo_bold, FontWeight.Bold),
    Font(R.font.cairo_semibold, FontWeight.SemiBold),
    Font(R.font.cairo_regular, FontWeight.Normal),
    Font(R.font.cairo_medium, FontWeight.Medium)
)

val cairoTypography = AppTypography(
    headline = SizedTextStyle(
        large = TextStyle(
            fontFamily = cairo,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = (36).sp,
            letterSpacing = 0.sp
        ),
        medium = TextStyle(
            fontFamily = cairo,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,           // font-3xl
            lineHeight = (28 * 1.25).sp,
            letterSpacing = 0.sp
        ),
        small = TextStyle(
            fontFamily = cairo,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,           // font-2xl
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        )
    ),
    body = SizedTextStyle(
        large = TextStyle(
            fontFamily = cairo,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,           // font-lg
            lineHeight = (18 * 1.5).sp,  // line-height-1-5
            letterSpacing = 0.sp
        ),
        medium = TextStyle(
            fontFamily = cairo,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = (16 * 1.5).sp,
            letterSpacing = 0.sp
        ),
        small = TextStyle(
            fontFamily = cairo,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,           // font-sm
            lineHeight = (24).sp,
            letterSpacing = 0.sp
        )
    ),
    label = SizedTextStyle(
        large = TextStyle(
            fontFamily = cairo,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,           // font-sm
            lineHeight = (14 * 1.75).sp,
            letterSpacing = 0.sp
        ),
        medium = TextStyle(
            fontFamily = cairo,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = (16).sp,
            letterSpacing = 0.sp
        ),
        small = TextStyle(
            fontFamily = cairo,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            lineHeight = (10 * 1.75).sp,
            letterSpacing = 0.sp
        )
    )
)

val LocalTypography = staticCompositionLocalOf { cairoTypography }