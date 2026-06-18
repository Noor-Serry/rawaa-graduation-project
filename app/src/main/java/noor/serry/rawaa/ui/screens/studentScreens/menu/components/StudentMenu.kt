package noor.serry.rawaa.ui.screens.studentScreens.menu.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuInteractionListener
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuItemCard
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuUiState

/**
 * Side-drawer panel content.
 * Fills the full height of the drawer slot provided by [MenuScreen].
 * Rounded corners on the LEFT edge only (leading edge for RTL — the
 * right side is flush against the screen edge).
 */
@Composable
fun StudentMenu(
    state: MenuUiState,
    interactionListener: MenuInteractionListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(
                color = AppTheme.color.bg,
                shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
            )
            .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {

        // ── Scrollable top section ────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {

            // User banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.color.primary)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                // Avatar
                Box(
                    modifier         = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(AppTheme.color.secondary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = state.userInitial,
                        color = AppTheme.color.primary,
                        style = AppTheme.textStyle.headline.small,
                    )
                }

                // Name + role
                Column(
                    modifier              = Modifier.weight(1f),
                    verticalArrangement   = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text  = state.userName,
                        color = AppTheme.color.bg,
                        style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text  = state.userRole,
                        color = AppTheme.color.bgSecondary,
                        style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                    )
                }

                // Close (X) button
                Icon(
                    painter  = painterResource(R.drawable.outline_close_24),
                    tint     = AppTheme.color.bg.copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(22.dp)
                        .clickAnimation(interactionListener::onMenuDismiss),
                )
            }

            // Version / copyright info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text  = stringResource(R.string.version_info),
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                )
                Text(
                    text  = stringResource(R.string.copyright),
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.medium,
                )
            }
        }

        // ── Logout button — always pinned at the bottom ───────────────────
        val strokeColor = AppTheme.color.border
        BaseButton(
            text            = stringResource(R.string.logout),
            onClick         = interactionListener::onLogoutClick,
            modifier        = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 1.17.dp.toPx()
                    val y = strokeWidth / 2
                    drawLine(
                        color       = strokeColor,
                        start       = androidx.compose.ui.geometry.Offset(0f, y),
                        end         = androidx.compose.ui.geometry.Offset(size.width, y),
                        strokeWidth = strokeWidth,
                    )
                }
                .navigationBarsPadding()
                .padding(16.dp),
            roundedCornerSize = 12.dp,
            icon              = painterResource(R.drawable.logout),
            borderColor       = AppTheme.color.errorBg,
            backgroundColor   = AppTheme.color.errorBg,
            isMirror          = false,
            textColor         = AppTheme.color.error,
        )
    }
}
