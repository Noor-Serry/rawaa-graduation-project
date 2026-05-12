package noor.serry.rawaa.ui.navigation.admin

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

enum class AdminNavTab {
    HOME, USERS, COURSES, REPORTS, SETTINGS
}

data class AdminBottomNavItem(
    val tab: AdminNavTab,
    val iconRes: Int,
    val labelRes: Int,
    val route: AdminRouteKeys,
)

@Composable
fun HomeAdminBottomNav(
    selectedTab: AdminNavTab,
    onTabSelected: (AdminBottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        AdminBottomNavItem(AdminNavTab.HOME,     R.drawable.ic_home,       R.string.nav_home,      AdminRouteKeys.Home),
        AdminBottomNavItem(AdminNavTab.USERS,    R.drawable.ic_person,     R.string.admin,     AdminRouteKeys.Users),
        AdminBottomNavItem(AdminNavTab.COURSES,  R.drawable.ic_book,       R.string.nav_courses,   AdminRouteKeys.Courses),
        AdminBottomNavItem(AdminNavTab.REPORTS,  R.drawable.badge,         R.string.nav_reports,   AdminRouteKeys.Reports),
        AdminBottomNavItem(AdminNavTab.SETTINGS, R.drawable.person,        R.string.admin,  AdminRouteKeys.Settings),
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
                AdminBottomNavItemView(
                    item       = item,
                    isSelected = selectedTab == item.tab,
                    onClick    = { onTabSelected(item) },
                )
            }
        }
    }
}

@Composable
private fun AdminBottomNavItemView(
    item: AdminBottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val iconTint   = if (isSelected) AppTheme.color.primary else AppTheme.color.textSecondary
    val labelColor = if (isSelected) AppTheme.color.primary else AppTheme.color.textSecondary
    val bgColor    = if (isSelected) AppTheme.color.primary.copy(alpha = 0.08f) else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Box(
            modifier = Modifier
                .background(bgColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(
                onClick  = onClick,
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    painter            = painterResource(item.iconRes),
                    contentDescription = stringResource(item.labelRes),
                    tint               = iconTint,
                    modifier           = Modifier.size(22.dp),
                )
            }
        }
        Text(
            text       = stringResource(item.labelRes),
            fontSize   = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color      = labelColor,
        )
    }
}
