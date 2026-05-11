package noor.serry.rawaa.ui.screens.schedule.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.schedule.SessionItem
import noor.serry.rawaa.ui.screens.schedule.SessionType

// ── Color tokens per session type ─────────────────────────────────────────────
//private data class SessionColors(
//    val accent: Color,
//    val background: Color,
//)

//private fun sessionColors(type: SessionType): SessionColors = when (type) {
//    SessionType.LECTURE -> SessionColors(
//        accent = Color(0xFF3B82F6),
//        background = Color(0x203B82F6)
//    )
//    SessionType.LAB -> SessionColors(
//        accent    = Color(0xFF10B981),
//        background = Color(0x2010B981)
//    )
//}
//
//@Composable
//fun SessionCard(
//    item: SessionItem,
//    onViewDetails: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    val colors = sessionColors(item.type)
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .background(AppTheme.color.bg,RoundedCornerShape(16.dp))
//            .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        // ── Top row: badge + name + calendar icon ──────────────────
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            verticalAlignment = Alignment.Top,
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(48.dp)
//                    .clip(RoundedCornerShape(12.dp))
//                    .background(colors.background),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.ic_calendar),
//                    contentDescription = null,
//                    tint = colors.accent,
//                    modifier = Modifier.size(24.dp)
//                )
//            }
//            Column(
//                modifier = Modifier.weight(1f),
//                verticalArrangement = Arrangement.spacedBy(2.dp)
//            ) {
//                Text(
//                    text = item.courseName,
//                    color = AppTheme.color.primaryDark,
//                    style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
//                    modifier = Modifier.padding(top = 4.dp)
//                )
//                Text(
//                    text = item.courseCode,
//                    color = AppTheme.color.textSecondary,
//                    style = AppTheme.textStyle.label.medium,
//                )
//            }
//
//            Box(
//                modifier = Modifier
//                    .background(colors.background,RoundedCornerShape(8.dp))
//                    .padding(horizontal = 8.dp, vertical = 4.dp)
//            ) {
//                Text(
//                    text = item.type.label,
//                    color = colors.accent,
//                    style = AppTheme.textStyle.label.small.copy(
//                        fontWeight = FontWeight.Medium
//                    ),
//                )
//            }
//        }
//
//        // ── Meta info column ───────────────────────────────────────
//        Column(
//            modifier = Modifier.fillMaxWidth().background(AppTheme
//                .color.bgHover,RoundedCornerShape(12.dp)).padding(12.dp)
//            ,verticalArrangement = Arrangement.spacedBy(6.dp)) {
//            // Time
//            SessionMetaRow(
//                iconRes = R.drawable.ic_clock,
//                text = item.timeRange,
//                tint = AppTheme.color.textSecondary,
//                textColor = AppTheme.color.primaryDark,
//                textStyle = AppTheme.textStyle.label.large.copy(fontWeight = FontWeight.Bold)
//            )
//            // Location
//            SessionMetaRow(
//                iconRes = R.drawable.mappin,
//                text = item.location,
//                tint = AppTheme.color.textSecondary,
//            )
//            // Professor
//            SessionMetaRow(
//                iconRes = R.drawable.ic_person,
//                text = item.professorName,
//                tint = AppTheme.color.textSecondary,
//            )
//        }
//
//        // ── View details button ────────────────────────────────────
//        BaseButton(
//            text = "عرض تفاصيل المحاضرة",
//            onClick = onViewDetails,
//            icon = painterResource(R.drawable.chevronleft),
//            iconColor = colors.accent,
//            backgroundColor = colors.background,
//            textColor = colors.accent,
//            iconAtEnd = true,
//            isMirror = false,
//            textStyle = AppTheme.textStyle.body.medium,
//            roundedCornerSize = 10.dp,
//            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 40.dp)
//        )
//    }
//}

@Composable
private fun SessionMetaRow(
    iconRes: Int,
    text: String,
    tint: Color,
    modifier: Modifier = Modifier,
    textColor : Color = AppTheme.color.primaryDark,
    textStyle : TextStyle =  AppTheme.textStyle.label.large.copy(fontWeight = FontWeight.Normal)
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            color = textColor ,
            style = textStyle,
        )
    }
}
