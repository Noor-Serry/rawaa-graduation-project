package noor.serry.rawaa.ui.screens.grading_teacher.components//package noor.serry.rawaa.ui.screens.grading_teacher.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import noor.serry.designsystem.design.AppTheme
//import noor.serry.rawaa.R
//
///**
// * Card for a single assignment on the grading list screen.
// *
// * Displays only fields that come from GET /api/doctor/dashboard:
// *   • title, courseName      ← DoctorAssignmentDto
// *   • deadline, totalPoints  ← DoctorAssignmentDto
// *   • gradedSubmissions / totalSubmissions → progress bar (client-computed %)
// *   • avgGradingMinutes      ← DoctorAssignmentDto (hidden when 0)
// *
// * No action buttons — there is no backend endpoint to initiate or
// * navigate to individual submission grading from this screen.
// */
//@Composable
//fun AssignmentGradingCard(
//    assignment: PendingAssignmentUiModel,
//    modifier: Modifier = Modifier
//) {
//    val colors    = AppTheme.color
//    val textStyle = AppTheme.textStyle
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(12.dp))
//            .background(colors.bg)
//            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
//            .padding(16.dp)
//    ) {
//        // ── Header: icon + title + course name ────────────────────────
//        Row(
//            modifier              = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment     = Alignment.Top
//        ) {
//            // Icon badge
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(colors.warningBg),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    painter            = painterResource(R.drawable.ic_assignment),
//                    contentDescription = null,
//                    tint               = colors.warning,
//                    modifier           = Modifier.size(22.dp)
//                )
//            }
//
//            // Title + course — RTL: aligned to end
//            Column(horizontalAlignment = Alignment.End) {
//                Text(
//                    text  = assignment.title,
//                    style = textStyle.body.large,
//                    color = colors.text
//                )
//                Spacer(Modifier.height(2.dp))
//                Text(
//                    text  = assignment.courseName,
//                    style = textStyle.label.medium,
//                    color = colors.textSecondary
//                )
//            }
//        }
//
//        Spacer(Modifier.height(10.dp))
//
//        // ── Deadline + points ─────────────────────────────────────────
//        Row(
//            modifier              = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.End,
//            verticalAlignment     = Alignment.CenterVertically
//        ) {
//            Text(
//                text  = "${assignment.totalPoints} نقطة",
//                style = textStyle.label.medium,
//                color = colors.textSecondary
//            )
//            Spacer(Modifier.width(16.dp))
//            Icon(
//                painter            = painterResource(R.drawable.ic_clock),
//                contentDescription = null,
//                tint               = colors.textSecondary,
//                modifier           = Modifier.size(14.dp)
//            )
//            Spacer(Modifier.width(4.dp))
//            Text(
//                text  = assignment.deadline,
//                style = textStyle.label.medium,
//                color = colors.textSecondary
//            )
//        }
//
//        Spacer(Modifier.height(12.dp))
//        HorizontalDivider(color = colors.border, thickness = 1.dp)
//        Spacer(Modifier.height(12.dp))
//
//        // ── Grading progress ──────────────────────────────────────────
//        GradingProgressBar(
//            gradedSubmissions = assignment.gradedSubmissions,
//            totalSubmissions  = assignment.totalSubmissions,
//            progress          = assignment.gradingProgress
//        )
//
//        Spacer(Modifier.height(8.dp))
//
//        // ── Average grading time (hidden when server returns 0) ───────
//        AverageGradingTimeRow(minutes = assignment.avgGradingMinutes)
//    }
//}
