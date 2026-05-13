package noor.serry.rawaa.ui.navigation.university_admin

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

// ── Tabs ─────────────────────────────────────────────────────────────────────

enum class UniversityAdminNavTab {
    DASHBOARD, USERS, COURSES, DEPARTMENTS, MORE
}

data class UniversityAdminBottomNavItem(
    val tab: UniversityAdminNavTab,
    val iconRes: Int,
    val labelRes: Int,
    val route: UniversityAdminRouteKeys,
)

// ── Bottom nav composable ─────────────────────────────────────────────────────

@Composable
fun UniversityAdminBottomNav(
    selectedTab: UniversityAdminNavTab,
    onTabSelected: (UniversityAdminBottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        UniversityAdminBottomNavItem(
            tab      = UniversityAdminNavTab.DASHBOARD,
            iconRes  = R.drawable.ic_home,
            labelRes = R.string.nav_home,
            route    = UniversityAdminRouteKeys.Dashboard,
        ),
        UniversityAdminBottomNavItem(
            tab      = UniversityAdminNavTab.USERS,
            iconRes  = R.drawable.ic_person,          // use your existing users/students icon
            labelRes = R.string.nav_users,
            route    = UniversityAdminRouteKeys.Users,
        ),
        UniversityAdminBottomNavItem(
            tab      = UniversityAdminNavTab.COURSES,
            iconRes  = R.drawable.ic_assignment,           // use your existing courses icon
            labelRes = R.string.nav_courses,
            route    = UniversityAdminRouteKeys.Courses,
        ),
        UniversityAdminBottomNavItem(
            tab      = UniversityAdminNavTab.DEPARTMENTS,
            iconRes  = R.drawable.university,        // add drawable res
            labelRes = R.string.nav_departments,
            route    = UniversityAdminRouteKeys.Departments,
        ),
        UniversityAdminBottomNavItem(
            tab      = UniversityAdminNavTab.MORE,
            iconRes  = R.drawable.settings,              // use your existing menu/more icon
            labelRes = R.string.nav_profile,
            route    = UniversityAdminRouteKeys.Settings,
        ),
    )

    Surface(
        modifier        = modifier.fillMaxWidth(),
        shadowElevation = 12.dp,
        color           = Color.White,
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                UniversityAdminNavItemView(
                    item       = item,
                    isSelected = selectedTab == item.tab,
                    onClick    = { onTabSelected(item) },
                )
            }
        }
    }
}

@Composable
private fun UniversityAdminNavItemView(
    item: UniversityAdminBottomNavItem,
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
            modifier         = Modifier
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

// Helper: derive tab from a route key
fun UniversityAdminRouteKeys.toNavTab(): UniversityAdminNavTab = when (this) {
    UniversityAdminRouteKeys.Dashboard   -> UniversityAdminNavTab.DASHBOARD
    UniversityAdminRouteKeys.Users,
    is UniversityAdminRouteKeys.StudentDetail,
    is UniversityAdminRouteKeys.EmployeeDetail -> UniversityAdminNavTab.USERS
    UniversityAdminRouteKeys.Courses     -> UniversityAdminNavTab.COURSES
    UniversityAdminRouteKeys.Departments       -> UniversityAdminNavTab.DEPARTMENTS
    else                                       -> UniversityAdminNavTab.MORE
}
