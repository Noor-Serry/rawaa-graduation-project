package noor.serry.rawaa.ui.screens.studentScreens.menu.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuInteractionListener
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuItemCard
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuUiState

@Composable
fun StudentMenu(
    state: MenuUiState,
    interactionListener: MenuInteractionListener,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.color.bg),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(136.dp)
                    .background(AppTheme.color.primary)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.End,
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(136.dp)
                        .background(AppTheme.color.primary)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(AppTheme.color.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.userInitial,
                            color = AppTheme.color.primary,
                            style = AppTheme.textStyle.headline.small,
                        )
                    }
                    Column() {
                        Text(
                            text = state.userName,
                            color = AppTheme.color.bg,
                            style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = state.userRole,
                            color = AppTheme.color.bgSecondary,
                            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                        )
                    }
                }
            }

            // Menu items
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                MenuItemCard(
                    label = stringResource(R.string.settings),
                    icon = painterResource(R.drawable.settings),
                    onClick = interactionListener::onSettingsClick,
                )

                MenuItemCard(
                    label = stringResource(R.string.help_and_support),
                    icon = painterResource(R.drawable.helpcircle),
                    onClick = interactionListener::onHelpAndSupportClick,
                )

                MenuItemCard(
                    label = stringResource(R.string.privacy_policy),
                    icon = painterResource(R.drawable.shield),
                    onClick = interactionListener::onPrivacyPolicyClick,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(
                    4.dp
                )
            ) {
                Text(
                    text = stringResource(R.string.version_info),
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                )
                Text(
                    text = stringResource(R.string.copyright),
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.medium
                )
            }
        }
        // Logout button
        val strokeColor = AppTheme.color.border
        BaseButton(
            text = stringResource(R.string.logout),
            onClick = interactionListener::onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind({
                    val strokeWidth = 1.17.dp.toPx()
                    val y = strokeWidth / 2
                    drawLine(
                        color = strokeColor,
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                })
                .padding(16.dp),
            roundedCornerSize = 12.dp,
            icon = painterResource(R.drawable.logout),
            borderColor = AppTheme.color.errorBg,
            backgroundColor = AppTheme.color.errorBg,
            isMirror = false,
            textColor = AppTheme.color.error
        )
    }
}