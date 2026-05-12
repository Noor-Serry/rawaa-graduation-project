package noor.serry.rawaa.ui.screens.users_admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.admin.AdminBackStackProvider
import noor.serry.rawaa.ui.navigation.admin.AdminRouteKeys

// ─────────────────────────────────────────────────────────────────────────────
// Pagination constants
// ─────────────────────────────────────────────────────────────────────────────

private const val PAGE_SIZE = 6

// ─────────────────────────────────────────────────────────────────────────────
// Entry point
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun UsersAdminScreen(
    viewModel: UsersAdminViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(effects = viewModel.effect)

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.color.primary)
            }
        }
        state.errorMessage != null -> {
            UsersErrorState(
                message = state.errorMessage ?: "",
                onRetry = viewModel::load,
            )
        }
        else -> {
            UsersAdminContent(state = state, listener = viewModel)
        }
    }

    // ── Delete confirmation dialog ─────────────────────────────────────────────
    if (state.pendingDeleteId != null) {
        DeleteConfirmDialog(
            onConfirm = viewModel::onDeleteConfirmed,
            onDismiss = viewModel::onDeleteDismissed,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UsersAdminContent(
    state: UsersAdminUiState,
    listener: UsersAdminInteractionListener,
) {
    // Pagination state – local to this composable only
    var currentPage by remember { mutableIntStateOf(1) }
    val listState = rememberLazyListState()

    val filteredUsers   = state.filteredUsers
    val totalPages      = maxOf(1, (filteredUsers.size + PAGE_SIZE - 1) / PAGE_SIZE)
    val pagedUsers      = filteredUsers
        .drop((currentPage - 1) * PAGE_SIZE)
        .take(PAGE_SIZE)

    // Reset to page 1 when filters change
    LaunchedEffect(state.searchQuery, state.selectedRole, state.selectedDepartmentId) {
        currentPage = 1
    }

    LazyColumn(
        state          = listState,
        modifier       = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        // ── Hero header ───────────────────────────────────────────────────────
        item {
            UsersHeroHeader(state = state, listener = listener)
        }

        // ── Search + filter row ───────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                UsersSearchBar(
                    query     = state.searchQuery,
                    onChanged = listener::onSearchQueryChanged,
                    modifier  = Modifier.weight(1f),
                )
                // Filter icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppTheme.color.bg)
                        .border(1.dp, AppTheme.color.border.copy(alpha = .6f), RoundedCornerShape(12.dp))
                        .clickAnimation { listener.onDepartmentFilterSelected(null) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter  = painterResource(R.drawable.filter),
                        tint     = AppTheme.color.textSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        // ── Role filter chips ─────────────────────────────────────────────────
        item {
            RoleFilterRow(
                selected   = state.selectedRole,
                counts     = mapOf(
                    UsersAdminUiState.RoleFilter.ALL     to state.totalCount,
                    UsersAdminUiState.RoleFilter.ADMIN   to state.adminCount,
                    UsersAdminUiState.RoleFilter.DOCTOR  to state.doctorCount,
                    UsersAdminUiState.RoleFilter.STUDENT to state.studentCount,
                ),
                onSelected = listener::onRoleFilterSelected,
                modifier   = Modifier.padding(bottom = 10.dp),
            )
        }

        // ── Department chips ──────────────────────────────────────────────────
        if (state.departments.isNotEmpty()) {
            item {
                DepartmentFilterRow(
                    departments          = state.departments,
                    selectedDepartmentId = state.selectedDepartmentId,
                    onSelected           = listener::onDepartmentFilterSelected,
                    modifier             = Modifier.padding(bottom = 10.dp),
                )
            }
        }

        // ── Empty state ───────────────────────────────────────────────────────
        if (filteredUsers.isEmpty()) {
            item {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = "لا توجد نتائج",
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.medium,
                    )
                }
            }
        }

        // ── Paged user cards ──────────────────────────────────────────────────
        items(pagedUsers, key = { "${it.userType.name}_${it.id}" }) { user ->
            UserCard(
                item     = user,
                listener = listener,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 10.dp),
            )
        }

        // ── Pagination bar ────────────────────────────────────────────────────
        if (totalPages > 1) {
            item {
                PaginationBar(
                    currentPage = currentPage,
                    totalPages  = totalPages,
                    onPageSelected = { page ->
                        currentPage = page
                    },
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UsersHeroHeader(
    state: UsersAdminUiState,
    listener: UsersAdminInteractionListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.color.primary)
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Title
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text     = "إدارة المستخدمين",
                color    = AppTheme.color.bg,
                style    = AppTheme.textStyle.headline.small,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
            Text(
                text      = "إدارة حسابات المستخدمين والصلاحيات",
                color     = AppTheme.color.bg.copy(alpha = .75f),
                style     = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                modifier  = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        }

        // 4 stat chips – order from design: إدارة / مدرس / طالب / الكل
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier.fillMaxWidth(),
        ) {
            HeroStatChip(count = state.adminCount,   label = "إدارة",  modifier = Modifier.weight(1f))
            HeroStatChip(count = state.doctorCount,  label = "مدرس",   modifier = Modifier.weight(1f))
            HeroStatChip(count = state.studentCount, label = "طالب",   modifier = Modifier.weight(1f))
            HeroStatChip(count = state.totalCount,   label = "الكل",   modifier = Modifier.weight(1f))
        }

        // Add-user button
        BaseButton(
            text              = "إضافة مستخدم جديد",
            onClick           = listener::onAddUserClicked,
            roundedCornerSize = 10.dp,
            icon              = painterResource(R.drawable.ic_add_person),
            backgroundColor   = AppTheme.color.bg,
            textColor         = AppTheme.color.primary,
            isMirror          = false,
        )
    }
}

@Composable
private fun HeroStatChip(count: Int, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AppTheme.color.bg.copy(alpha = .14f))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text  = "$count",
            color = AppTheme.color.bg,
            style = AppTheme.textStyle.headline.small,
        )
        Text(
            text  = label,
            color = AppTheme.color.bg.copy(alpha = .8f),
            style = AppTheme.textStyle.label.small,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Search bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UsersSearchBar(
    query: String,
    onChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .6f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter  = painterResource(R.drawable.search),
            tint     = AppTheme.color.textSecondary,
            modifier = Modifier.size(18.dp),
        )
        BasicTextField(
            value         = query,
            onValueChange = onChanged,
            modifier      = Modifier.weight(1f),
            textStyle     = AppTheme.textStyle.body.small.copy(color = AppTheme.color.text),
            singleLine    = true,
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        text  = "بحث بالاسم أو البريد الإلكتروني...",
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.small,
                    )
                }
                inner()
            },
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Role filter chips
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RoleFilterRow(
    selected: UsersAdminUiState.RoleFilter,
    counts: Map<UsersAdminUiState.RoleFilter, Int>,
    onSelected: (UsersAdminUiState.RoleFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier              = modifier,
        contentPadding        = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(UsersAdminUiState.RoleFilter.entries) { filter ->
            val count = counts[filter] ?: 0
            FilterChip(
                label      = "${filter.labelAr} ($count)",
                isSelected = selected == filter,
                onClick    = { onSelected(filter) },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Department filter chips
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DepartmentFilterRow(
    departments: List<UsersAdminUiState.DepartmentFilterItem>,
    selectedDepartmentId: Int?,
    onSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier              = modifier,
        contentPadding        = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                label      = "الكل",
                isSelected = selectedDepartmentId == null,
                onClick    = { onSelected(null) },
            )
        }
        items(departments) { dept ->
            FilterChip(
                label      = dept.name,
                isSelected = selectedDepartmentId == dept.id,
                onClick    = { onSelected(dept.id) },
            )
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) AppTheme.color.primary else AppTheme.color.bg)
            .border(
                width = 1.dp,
                color = if (isSelected) AppTheme.color.primary else AppTheme.color.border,
                shape = RoundedCornerShape(20.dp),
            )
            .clickAnimation(onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text  = label,
            color = if (isSelected) AppTheme.color.bg else AppTheme.color.textSecondary,
            style = AppTheme.textStyle.label.medium,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// User card  (matches the design precisely)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UserCard(
    item: UsersAdminUiState.UserItem,
    listener: UsersAdminInteractionListener,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), ambientColor = Color.Black.copy(alpha = .06f))
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Card header row ───────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 10.dp),
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Right side: 3-dot menu
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { /* dropdown menu – future work */ },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter  = painterResource(R.drawable.university),
                        tint     = AppTheme.color.textSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }

                // Left side: name + badges + department
                Column(
                    modifier              = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    horizontalAlignment   = Alignment.End,
                    verticalArrangement   = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text      = item.name,
                        color     = AppTheme.color.text,
                        style     = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                        maxLines  = 1,
                        overflow  = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                    )
                    // Badges row: status + role
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        RoleBadge(userType = item.userType, role = item.role)
                        ActiveBadge(isActive = item.isActive)
                    }
                    // Department
                    item.departmentName?.let {
                        Text(
                            text      = it,
                            color     = AppTheme.color.textSecondary,
                            style     = AppTheme.textStyle.label.medium,
                            textAlign = TextAlign.End,
                        )
                    }
                }

                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(avatarColor(item.userType)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = item.name.take(1),
                        color = Color.White,
                        style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }

            Divider(color = AppTheme.color.border.copy(alpha = .5f), thickness = 0.8.dp)

            // ── Info rows: email / phone / join date ──────────────────────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.End,
            ) {
                InfoRow(iconRes = noor.serry.designsystem.R.drawable.mail, text = item.email)
                item.phone?.let { InfoRow(iconRes = R.drawable.ic_phone, text = it) }
                item.createdAt?.let {
                    InfoRow(
                        iconRes = R.drawable.ic_clock,
                        text    = "انضم في ${formatJoinYear(it)}",
                    )
                }
            }

            Divider(color = AppTheme.color.border.copy(alpha = .5f), thickness = 0.8.dp)

            // ── Action buttons: عرض الملف / تعديل / حذف ──────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // حذف (delete) – left-most, red
                ActionButton(
                    label     = "حذف",
                    iconRes   = R.drawable.ic_trash,
                    textColor = Color(0xFFDC2626),
                    bgColor   = Color(0xFFFEE2E2),
                    onClick   = { listener.onDeleteClicked(item.id, item.userType) },
                )
                // تعديل (edit) – neutral
                ActionButton(
                    label     = "تعديل",
                    iconRes   = R.drawable.ic_edit,
                    textColor = AppTheme.color.textSecondary,
                    bgColor   = AppTheme.color.bgHover,
                    modifier  = Modifier.weight(1f),
                    onClick   = { listener.onViewProfileClicked(item.id, item.userType) },
                )
                // عرض الملف (view profile) – right-most, neutral
                ActionButton(
                    label     = "عرض الملف",
                    iconRes   = noor.serry.designsystem.R.drawable.password_eye_open,
                    textColor = AppTheme.color.textSecondary,
                    bgColor   = AppTheme.color.bgHover,
                    modifier  = Modifier.weight(1f),
                    onClick   = { listener.onViewProfileClicked(item.id, item.userType) },
                )
            }
        }
    }
}

@Composable
private fun InfoRow(iconRes: Int, text: String) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text     = text,
            color    = AppTheme.color.textSecondary,
            style    = AppTheme.textStyle.label.small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(
            painter  = painterResource(iconRes),
            tint     = AppTheme.color.textSecondary,
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun ActionButton(
    label: String,
    iconRes: Int,
    textColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickAnimation(onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Icon(
            painter  = painterResource(iconRes),
            tint     = textColor,
            modifier = Modifier.size(14.dp),
        )
        Text(text = label, color = textColor, style = AppTheme.textStyle.label.medium)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Badges
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ActiveBadge(isActive: Boolean) {
    val bg    = if (isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
    val tint  = if (isActive) Color(0xFF16A34A) else Color(0xFFDC2626)
    val label = if (isActive) "نشط" else "موقوف"
    Badge(label = label, bg = bg, tint = tint)
}

@Composable
private fun RoleBadge(userType: UsersAdminUiState.UserType, role: String) {
    val (bg, tint, label) = when (userType) {
        UsersAdminUiState.UserType.STUDENT -> Triple(Color(0xFFEFF6FF), Color(0xFF1D4ED8), "🎓 طالب")
        UsersAdminUiState.UserType.DOCTOR  -> Triple(Color(0xFFFEF9C3), Color(0xFFB45309), "👨‍🏫 مدرس")
        UsersAdminUiState.UserType.ADMIN   -> Triple(Color(0xFFF3F4F6), Color(0xFF374151), role)
    }
    Badge(label = label, bg = bg, tint = tint)
}

@Composable
private fun Badge(label: String, bg: Color, tint: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(text = label, color = tint, style = AppTheme.textStyle.label.small.copy(fontSize = 10.sp))
    }
}

private fun avatarColor(type: UsersAdminUiState.UserType): Color = when (type) {
    UsersAdminUiState.UserType.STUDENT -> Color(0xFF1D4ED8)
    UsersAdminUiState.UserType.DOCTOR  -> Color(0xFFB45309)
    UsersAdminUiState.UserType.ADMIN   -> Color(0xFF374151)
}

// ─────────────────────────────────────────────────────────────────────────────
// Pagination bar  (matches design: السابق / 1 / 2 / 3 / التالي)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        // السابق
        PaginationButton(
            label     = "السابق",
            enabled   = currentPage > 1,
            isActive  = false,
            onClick   = { if (currentPage > 1) onPageSelected(currentPage - 1) },
        )

        // Page numbers (show up to 5 around current page)
        val pageRange = buildPageRange(currentPage, totalPages)
        pageRange.forEach { page ->
            PaginationButton(
                label    = "$page",
                enabled  = true,
                isActive = page == currentPage,
                onClick  = { onPageSelected(page) },
            )
        }

        // التالي
        PaginationButton(
            label   = "التالي",
            enabled = currentPage < totalPages,
            isActive = false,
            onClick = { if (currentPage < totalPages) onPageSelected(currentPage + 1) },
        )
    }
}

@Composable
private fun PaginationButton(
    label: String,
    enabled: Boolean,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val bg     = when {
        isActive  -> AppTheme.color.primary
        !enabled  -> AppTheme.color.bgHover
        else      -> AppTheme.color.bg
    }
    val textColor = when {
        isActive -> AppTheme.color.bg
        !enabled -> AppTheme.color.textSecondary.copy(alpha = .4f)
        else     -> AppTheme.color.text
    }
    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 40.dp, minHeight = 40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(
                width = 1.dp,
                color = if (isActive) AppTheme.color.primary else AppTheme.color.border,
                shape = RoundedCornerShape(10.dp),
            )
            .then(if (enabled) Modifier.clickAnimation(onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = label,
            color = textColor,
            style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Medium),
        )
    }
}

private fun buildPageRange(current: Int, total: Int): List<Int> {
    if (total <= 5) return (1..total).toList()
    val start = maxOf(1, minOf(current - 2, total - 4))
    val end   = minOf(total, start + 4)
    return (start..end).toList()
}

// ─────────────────────────────────────────────────────────────────────────────
// Delete confirmation dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DeleteConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text  = "تأكيد الحذف",
                color = AppTheme.color.text,
                style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
            )
        },
        text = {
            Text(
                text  = "هل أنت متأكد من حذف هذا المستخدم؟ لا يمكن التراجع عن هذه العملية.",
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.body.small,
            )
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFDC2626))
                    .clickable(onClick = onConfirm)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(text = "حذف", color = Color.White, style = AppTheme.textStyle.label.large)
            }
        },
        dismissButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppTheme.color.bgHover)
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(text = "إلغاء", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.large)
            }
        },
        containerColor = AppTheme.color.bg,
        shape          = RoundedCornerShape(16.dp),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Error state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UsersErrorState(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = message, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.medium)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.color.primary)
                    .clickAnimation(onRetry)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text(
                    text  = "إعادة المحاولة",
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Effects handler
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HandleEffects(effects: Flow<UsersAdminEffect>) {
    val backStack = AdminBackStackProvider.current
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                is UsersAdminEffect.NavigateToAddUser                -> backStack.add(AdminRouteKeys.)
                is UsersAdminEffect.NavigateToStudentDetail  -> { /* backStack.add(AppRoute.StudentDetail(effect.studentId)) */ }
                is UsersAdminEffect.NavigateToEmployeeDetail -> { /* backStack.add(AppRoute.EmployeeDetail(effect.employeeId)) */ }
                is UsersAdminEffect.ShowDeleteSuccess        -> { /* show toast/snackbar */ }
                is UsersAdminEffect.ShowError                -> { /* show toast/snackbar */ }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun formatJoinYear(createdAt: String): String =
    createdAt.take(4).ifBlank { createdAt }
