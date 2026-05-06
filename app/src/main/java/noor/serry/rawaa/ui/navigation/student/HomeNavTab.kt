package noor.serry.rawaa.ui.navigation.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.student.StudentRouteKeys

enum class HomeNavTab {
    HOME, COURSES, SCHEDULE, NOTIFICATIONS, PROFILE
}

data class BottomNavItem(
    val tab: HomeNavTab,
    val iconRes: Int,
    val labelRes: Int,
    val route : StudentRouteKeys
)

@Composable
fun HomeStudentBottomNav(
    selectedTab: HomeNavTab,
    onTabSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        BottomNavItem(HomeNavTab.HOME,          R.drawable.ic_home,          R.string.nav_home, StudentRouteKeys.Home),
        BottomNavItem(HomeNavTab.COURSES,       R.drawable.ic_book,          R.string.nav_courses, StudentRouteKeys.Courses),
        BottomNavItem(HomeNavTab.SCHEDULE,      R.drawable.ic_calendar,      R.string.nav_schedule, StudentRouteKeys.Schedule),
        BottomNavItem(HomeNavTab.NOTIFICATIONS, R.drawable.ic_bell,          R.string.nav_notifications, StudentRouteKeys.Notifications),
        BottomNavItem(HomeNavTab.PROFILE,       R.drawable.person,       R.string.nav_profile, StudentRouteKeys.Profile),
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 12.dp,
        color = Color.White,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                BottomNavItemView(
                    item = item,
                    isSelected = selectedTab == item.tab,
                    onClick = { onTabSelected(item) },
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val iconTint  = if (isSelected) AppTheme.color.primary else AppTheme.color.textSecondary
    val labelColor = if (isSelected) AppTheme.color.primary else AppTheme.color.textSecondary
    val bgColor   = if (isSelected) AppTheme.color.primary.copy(alpha = 0.08f) else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .let {
                if (isSelected) it else it
            }
    ) {
        Box(
            modifier = Modifier
                .background(bgColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    painter = painterResource(item.iconRes),
                    contentDescription = stringResource(item.labelRes),
                    tint = iconTint,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        Text(
            text = stringResource(item.labelRes),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = labelColor,
        )
    }
}