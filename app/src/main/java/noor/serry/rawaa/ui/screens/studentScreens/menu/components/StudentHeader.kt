package noor.serry.rawaa.ui.screens.studentScreens.menu.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuInteractionListener
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuUiState

@Composable
fun StudentHeader(
    state: MenuUiState,
    interactionListener: MenuInteractionListener,
    modifier: Modifier = Modifier
) {
    val menuItemRotate by animateFloatAsState(if (state.isOpen) 90f else 0f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(10f)
            .height(64.dp)
            .background(AppTheme.color.bg)
            .padding(horizontal = 16.dp).statusBarsPadding(),

        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(R.drawable.ic_menu), modifier = Modifier
                .size(
                    24.dp
                )
                .rotate(menuItemRotate)
                .clickAnimation(interactionListener::onMenuToggle),
            tint = AppTheme.color.textSecondary
        )

        Icon(
            painterResource(R.drawable.bell), modifier = Modifier
                .padding(
                    start = 8.dp
                )
                .size(
                    24.dp
                ),
            tint = AppTheme.color.textSecondary
        )

        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.rowaa),
                color = AppTheme.color.primary,
                style = AppTheme.textStyle.headline.small.copy(
                    fontSize = 20.sp
                ),
            )
            Text(
                stringResource(R.string.egyption_unversity),
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.label.medium
            )
        }
    }
}