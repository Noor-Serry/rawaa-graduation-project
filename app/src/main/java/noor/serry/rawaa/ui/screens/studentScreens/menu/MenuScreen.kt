package noor.serry.rawaa.ui.screens.studentScreens.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.rawaa.ui.navigation.student.StudentRouteKeys
import org.koin.androidx.compose.koinViewModel

private data class MenuItem(
    val icon: ImageVector,
    val label: String,
    val route: StudentRouteKeys,
)

private val MENU_ITEMS = listOf(
    MenuItem(Icons.Default.Person,        "الملف الشخصي",   StudentRouteKeys.Profile),
    MenuItem(Icons.Default.Book,          "مقرراتي",          StudentRouteKeys.Courses),
    MenuItem(Icons.Default.CalendarToday, "الجدول الأسبوعي",  StudentRouteKeys.Schedule),
    MenuItem(Icons.Default.Notifications, "الإشعارات",      StudentRouteKeys.Notifications),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: MenuViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val backStack = StudentBackStackProvider.current
    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) {}
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("القائمة") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            item {
                MENU_ITEMS.forEach { item ->
                    MenuRowItem(
                        icon = item.icon,
                        label = item.label,
                        onClick = {
                            backStack.add(item.route)
                           },
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // Logout
            item {
                state.errorMessage?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(Modifier.height(8.dp))
                }

                ListItem(
                    headlineContent = {
                        Text(
                            "تسجيل الخروج",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error,
                        )
                    },
                    leadingContent = {
                        if (state.isLoggingOut) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                    modifier = Modifier.clickable(enabled = !state.isLoggingOut) {
                        viewModel.logout()
                    },
                )
            }
        }
    }
}

@Composable
private fun MenuRowItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(label) },
        leadingContent  = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = Modifier.clickable(onClick = onClick),
    )
}
