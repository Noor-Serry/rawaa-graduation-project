package noor.serry.rawaa.ui.screens.universities_super_admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.super_admin.AdminBackStackProvider

// ─────────────────────────────────────────────────────────────────────────────
// Entry point
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun UniversitiesScreen(
    viewModel: UniversitiesViewModel = koinViewModel(),
) {
    val state        by viewModel.state.collectAsState()
    val snackbarHost = remember { SnackbarHostState() }
    val listState    = rememberLazyListState()

    val backstack = AdminBackStackProvider.current

    HandleEffects(
        effects          = viewModel.effect,
        snackbarHost     = snackbarHost,
        listState        = listState,
        onNavigateBack   = {backstack.removeLastOrNull()},
    )

    // Infinite scroll: trigger loadMore when near the end
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { lastVisible ->
                val total = listState.layoutInfo.totalItemsCount
                if (lastVisible != null && lastVisible >= total - 4) {
                    viewModel.onLoadMore()
                }
            }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.color.bgHover),
        ) {
            // ── Top bar ───────────────────────────────────────────────────────
            UniversitiesTopBar(
                totalCount = state.totalCount,
                onBack     = viewModel::onBackClick,
                onAdd      = viewModel::onCreateUniversityClick,
            )

            // ── Search + filters ──────────────────────────────────────────────
            SearchAndFiltersRow(state = state, listener = viewModel)

            // ── List ──────────────────────────────────────────────────────────
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppTheme.color.primary)
                    }
                }

                state.errorMessage != null && state.universities.isEmpty() -> {
                    UniversitiesErrorState(
                        message = state.errorMessage.orEmpty(),
                        onRetry = viewModel::onRetry,
                    )
                }

                state.universities.isEmpty() -> {
                    UniversitiesEmptyState()
                }

                else -> {
                    LazyColumn(
                        state                    = listState,
                        modifier                 = Modifier.fillMaxSize(),
                        contentPadding           = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement      = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = state.universities,
                            key   = { it.id },
                        ) { university ->
                            val isSelected = state.selectedUniversity?.base?.id == university.id

                            UniversityManagementCard(
                                university      = university,
                                isSelected      = isSelected,
                                isActionLoading = state.actionLoadingId == university.id,
                                listener        = viewModel,
                            )

                            // Animated detail / admins panel
                            AnimatedVisibility(
                                visible = isSelected,
                                enter   = fadeIn(tween(200)) + expandVertically(tween(300)),
                                exit    = fadeOut(tween(150)) + shrinkVertically(tween(250)),
                            ) {
                                UniversityDetailPanel(
                                    detail    = state.selectedUniversity,
                                    admins    = state.admins,
                                    isLoading = state.isDetailLoading || state.isAdminsLoading,
                                    onDismiss = viewModel::onDismissDetailPanel,
                                )
                            }
                        }

                        // Load-more spinner
                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        color    = AppTheme.color.primary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHost,
            modifier  = Modifier.align(Alignment.BottomCenter),
        )
    }

    // ── Create sheet ──────────────────────────────────────────────────────────
    if (state.showCreateSheet) {
        UniversityCreateSheet(state = state, listener = viewModel)
    }

    // ── Edit sheet ────────────────────────────────────────────────────────────
    if (state.showEditSheet) {
        UniversityEditSheet(state = state, listener = viewModel)
    }

    // ── Change-plan dialog ────────────────────────────────────────────────────
    if (state.showPlanDialog) {
        ChangePlanDialog(state = state, listener = viewModel)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UniversitiesTopBar(
    totalCount: Int,
    onBack: () -> Unit,
    onAdd: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.color.bg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        // Add button
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AppTheme.color.primary)
                .clickAnimation(onAdd),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter  = painterResource(R.drawable.outline_add_home_work_24),
                tint     = AppTheme.color.bg,
                modifier = Modifier.size(18.dp),
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text  = "إدارة الجامعات",
                color = AppTheme.color.text,
                style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
            )
            if (totalCount > 0) {
                Text(
                    text  = "$totalCount جامعة مسجلة",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.small,
                )
            }
        }

        // Back button
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AppTheme.color.bgHover)
                .clickAnimation(onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter  = painterResource(R.drawable.ic_arrow_forward),
                tint     = AppTheme.color.text,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Search + filters
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SearchAndFiltersRow(
    state: UniversitiesUiState,
    listener: UniversitiesInteractionListener,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.color.bg)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Search field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.color.bgHover)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BasicTextField(
                value         = state.searchQuery,
                onValueChange = listener::onSearchQueryChange,
                modifier      = Modifier.weight(1f),
                textStyle     = AppTheme.textStyle.body.small.copy(
                    color     = AppTheme.color.text,
                    textAlign = TextAlign.End,
                ),
                cursorBrush   = SolidColor(AppTheme.color.primary),
                decorationBox = { inner ->
                    if (state.searchQuery.isEmpty()) {
                        Text(
                            text  = "بحث عن جامعة...",
                            color = AppTheme.color.textSecondary,
                            style = AppTheme.textStyle.body.small,
                        )
                    }
                    inner()
                },
            )
            Icon(
                painter  = painterResource(R.drawable.search),
                tint     = AppTheme.color.textSecondary,
                modifier = Modifier.size(16.dp),
            )
        }

        // Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout         = true,
        ) {
            // Active filter chips
            item {
                FilterChip(
                    label     = "الكل",
                    selected  = state.filterIsActive == null && state.filterPlan == null,
                    onClick   = listener::onClearFilters,
                )
            }
            item {
                FilterChip(
                    label    = "نشط",
                    selected = state.filterIsActive == 1,
                    onClick  = { listener.onActiveFilterChange(1) },
                )
            }
            item {
                FilterChip(
                    label    = "موقوف",
                    selected = state.filterIsActive == 0,
                    onClick  = { listener.onActiveFilterChange(0) },
                )
            }
            val plans = listOf("trial" to "تجريبي", "basic" to "أساسي", "pro" to "برو", "enterprise" to "مؤسسي")
            items(plans) { (plan, label) ->
                FilterChip(
                    label    = label,
                    selected = state.filterPlan == plan,
                    onClick  = { listener.onPlanFilterChange(plan) },
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (selected) AppTheme.color.primary
                else AppTheme.color.bgHover
            )
            .border(
                width = 1.dp,
                color = if (selected) AppTheme.color.primary
                else AppTheme.color.border,
                shape = RoundedCornerShape(20.dp),
            )
            .clickAnimation(onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    ) {
        Text(
            text  = label,
            color = if (selected) AppTheme.color.bg else AppTheme.color.textSecondary,
            style = AppTheme.textStyle.label.small.copy(fontWeight = FontWeight.Medium),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// University management card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UniversityManagementCard(
    university: UniversitiesUiState.UniversityItem,
    isSelected: Boolean,
    isActionLoading: Boolean,
    listener: UniversitiesInteractionListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(
                width = 1.dp,
                color = if (isSelected)
                    AppTheme.color.primary.copy(alpha = .4f)
                else
                    AppTheme.color.border.copy(alpha = .6f),
                shape = RoundedCornerShape(16.dp),
            )
            .clickAnimation { listener.onUniversityClick(university.id) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // ── Row 1: name + status + plan ───────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            // Expand indicator
            Icon(
                painter  = painterResource(
                    if (isSelected) R.drawable.arrow_up else R.drawable.arrow_down
                ),
                tint     = AppTheme.color.textSecondary,
                modifier = Modifier.size(18.dp),
            )

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (university.plan != null) PlanPill(plan = university.plan)

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (university.isActiveBool) Color(0xFF22C55E)
                            else Color(0xFFEF4444)
                        ),
                )

                Text(
                    text      = university.name,
                    color     = AppTheme.color.text,
                    style     = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
                    maxLines  = 1,
                    overflow  = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
            }
        }

        // ── Row 2: stats ──────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            StatChip(count = university.studentCount,    label = "طالب")
            Spacer(Modifier.width(10.dp))
            StatChip(count = university.doctorCount,     label = "دكتور")
            Spacer(Modifier.width(10.dp))
            StatChip(count = university.departmentCount, label = "قسم")
        }

        HorizontalDivider(color = AppTheme.color.border.copy(alpha = .5f))

        // ── Row 3: action buttons ─────────────────────────────────────────────
        if (isActionLoading) {
            Box(
                modifier         = Modifier.fillMaxWidth().height(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(20.dp),
                    color       = AppTheme.color.primary,
                    strokeWidth = 2.dp,
                )
            }
        } else {
            // Top row: Update + Change Plan
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Update
                ActionLabelButton(
                    modifier = Modifier.weight(1f),
                    label    = "تعديل الجامعة",
                    iconRes  = R.drawable.university,
                    tint     = Color(0xFF3B82F6),
                    onClick  = { listener.onEditUniversityClick(university.id) },
                )
                // Change Plan
                ActionLabelButton(
                    modifier = Modifier.weight(1f),
                    label    = "تغيير الخطة",
                    iconRes  = R.drawable.shield,
                    tint     = Color(0xFF7C3AED),
                    onClick  = { listener.onChangePlanClick(university.id) },
                )
            }
            // Bottom row: Activate / Deactivate
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Activate
                ActionLabelButton(
                    modifier = Modifier.weight(1f),
                    label    = "تفعيل",
                    iconRes  = R.drawable.ic_trending_up,
                    tint     = Color(0xFF22C55E),
                    enabled  = !university.isActiveBool,
                    onClick  = { listener.onActivateUniversity(university.id) },
                )
                // Deactivate
                ActionLabelButton(
                    modifier = Modifier.weight(1f),
                    label    = "إيقاف",
                    iconRes  = R.drawable.arrow_down,
                    tint     = Color(0xFFEF4444),
                    enabled  = university.isActiveBool,
                    onClick  = { listener.onDeactivateUniversity(university.id) },
                )
            }
        }
    }
}

@Composable
private fun StatChip(count: Int, label: String) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text  = "$count $label",
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.label.small,
        )
    }
}

@Composable
private fun ActionIconButton(
    iconRes: Int,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(tint.copy(alpha = .1f))
            .clickAnimation(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter  = painterResource(iconRes),
            tint     = tint,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun ActionLabelButton(
    label: String,
    iconRes: Int,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val effectiveTint = if (enabled) tint else AppTheme.color.textSecondary.copy(alpha = .4f)
    val effectiveBg   = effectiveTint.copy(alpha = .08f)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(effectiveBg)
            .border(
                width = 1.dp,
                color = effectiveTint.copy(alpha = .25f),
                shape = RoundedCornerShape(10.dp),
            )
            .then(
                if (enabled) Modifier.clickAnimation(onClick)
                else Modifier
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text  = label,
            color = effectiveTint,
            style = AppTheme.textStyle.label.small.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize   = 11.sp,
            ),
            maxLines = 1,
        )
        Icon(
            painter  = painterResource(iconRes),
            tint     = effectiveTint,
            modifier = Modifier.size(14.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Detail / admins panel
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UniversityDetailPanel(
    detail: UniversitiesUiState.UniversityDetailItem?,
    admins: List<UniversitiesUiState.UniversityAdminItem>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.bg)
            .border(
                width = 1.dp,
                color = AppTheme.color.primary.copy(alpha = .2f),
                shape = RoundedCornerShape(14.dp),
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Panel header
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(AppTheme.color.bgHover)
                    .clickAnimation(onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter  = painterResource(R.drawable.outline_close_24),
                    tint     = AppTheme.color.textSecondary,
                    modifier = Modifier.size(14.dp),
                )
            }

            Text(
                text  = "تفاصيل الجامعة",
                color = AppTheme.color.text,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
            )
        }

        HorizontalDivider(color = AppTheme.color.border.copy(alpha = .5f))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(28.dp),
                        color       = AppTheme.color.primary,
                        strokeWidth = 2.dp,
                    )
                }
            }

            detail != null -> {
                // Stats row
                val base = detail.base
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    DetailStatBox(value = base.studentCount.toString(),    label = "طالب")
                    DetailStatBox(value = base.doctorCount.toString(),     label = "دكتور")
                    DetailStatBox(value = base.employeeCount.toString(),   label = "موظف")
                    DetailStatBox(value = base.departmentCount.toString(), label = "قسم")
                    DetailStatBox(value = base.activeCourses.toString(),   label = "مقرر")
                }

                if (admins.isNotEmpty()) {
                    HorizontalDivider(color = AppTheme.color.border.copy(alpha = .4f))

                    Text(
                        text      = "المشرفون (${admins.size})",
                        color     = AppTheme.color.text,
                        style     = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                        modifier  = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                    )

                    admins.forEachIndexed { index, admin ->
                        DetailAdminRow(admin = admin)
                        if (index < admins.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color    = AppTheme.color.border.copy(alpha = .3f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailStatBox(value: String, label: String) {
    Column(
        modifier            = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AppTheme.color.bgHover)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text  = value,
            color = AppTheme.color.text,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text  = label,
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.label.small,
        )
    }
}

@Composable
private fun DetailAdminRow(admin: UniversitiesUiState.UniversityAdminItem) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    if (admin.isActiveBool) Color(0xFF22C55E)
                    else Color(0xFFEF4444)
                ),
        )
        Spacer(Modifier.width(8.dp))
        Column(
            modifier            = Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text      = admin.name,
                color     = AppTheme.color.text,
                style     = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium),
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
            )
            Text(
                text      = admin.email,
                color     = AppTheme.color.textSecondary,
                style     = AppTheme.textStyle.label.small,
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
            )
        }
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AppTheme.color.primary.copy(alpha = .1f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = admin.name.firstOrNull()?.uppercaseChar()?.toString() ?: "؟",
                color = AppTheme.color.primary,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Create bottom sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UniversityCreateSheet(
    state: UniversitiesUiState,
    listener: UniversitiesInteractionListener,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest  = listener::onDismissCreateSheet,
        sheetState        = sheetState,
        containerColor    = AppTheme.color.bg,
    ) {
        UniversityFormContent(
            title           = "إضافة جامعة جديدة",
            form            = state.createForm,
            isCreate        = true,
            isLoading       = state.isLoading,
            onConfirm       = listener::onConfirmCreate,
            onDismiss       = listener::onDismissCreateSheet,
            onNameChange    = listener::onCreateFormNameChange,
            onNameEnChange  = listener::onCreateFormNameEnChange,
            onSlugChange    = listener::onCreateFormSlugChange,
            onEmailChange   = listener::onCreateFormEmailChange,
            onPhoneChange   = listener::onCreateFormPhoneChange,
            onAddressChange = listener::onCreateFormAddressChange,
            onCountryChange = listener::onCreateFormCountryChange,
            onPlanChange    = listener::onCreateFormPlanChange,
            onExpiresChange = listener::onCreateFormPlanExpiresAtChange,
            onMaxStudents   = listener::onCreateFormMaxStudentsChange,
            onMaxStaff      = listener::onCreateFormMaxStaffChange,
            onAdminName     = listener::onCreateFormAdminNameChange,
            onAdminEmail    = listener::onCreateFormAdminEmailChange,
            onAdminPassword = listener::onCreateFormAdminPasswordChange,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Edit bottom sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UniversityEditSheet(
    state: UniversitiesUiState,
    listener: UniversitiesInteractionListener,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = listener::onDismissEditSheet,
        sheetState       = sheetState,
        containerColor   = AppTheme.color.bg,
    ) {
        UniversityFormContent(
            title           = "تعديل بيانات الجامعة",
            form            = state.editForm,
            isCreate        = false,
            isLoading       = state.isLoading,
            onConfirm       = listener::onConfirmEdit,
            onDismiss       = listener::onDismissEditSheet,
            onNameChange    = listener::onEditFormNameChange,
            onNameEnChange  = listener::onEditFormNameEnChange,
            onSlugChange    = listener::onEditFormSlugChange,
            onEmailChange   = listener::onEditFormEmailChange,
            onPhoneChange   = listener::onEditFormPhoneChange,
            onAddressChange = listener::onEditFormAddressChange,
            onCountryChange = listener::onEditFormCountryChange,
            onPlanChange    = listener::onEditFormPlanChange,
            onExpiresChange = listener::onEditFormPlanExpiresAtChange,
            onMaxStudents   = listener::onEditFormMaxStudentsChange,
            onMaxStaff      = listener::onEditFormMaxStaffChange,
            onAdminName     = {},
            onAdminEmail    = {},
            onAdminPassword = {},
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared form content (used by both create & edit sheets)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun UniversityFormContent(
    title: String,
    form: UniversitiesUiState.UniversityFormState,
    isCreate: Boolean,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onNameEnChange: (String) -> Unit,
    onSlugChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onPlanChange: (String) -> Unit,
    onExpiresChange: (String) -> Unit,
    onMaxStudents: (String) -> Unit,
    onMaxStaff: (String) -> Unit,
    onAdminName: (String) -> Unit,
    onAdminEmail: (String) -> Unit,
    onAdminPassword: (String) -> Unit,
) {
    androidx.compose.foundation.lazy.LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues( 16.dp),
    ) {
        item {
            Text(
                text      = title,
                color     = AppTheme.color.text,
                style     = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
                modifier  = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        }

        item { FormField(label = "اسم الجامعة *",      value = form.name,   onValueChange = onNameChange,    error = form.nameError) }
        item { FormField(label = "الاسم بالإنجليزية",  value = form.nameEn, onValueChange = onNameEnChange) }
        item { FormField(label = "الرابط المختصر (slug) *", value = form.slug, onValueChange = onSlugChange, error = form.slugError) }
        item { FormField(label = "البريد الإلكتروني",  value = form.email,   onValueChange = onEmailChange) }
        item { FormField(label = "رقم الهاتف",          value = form.phone,   onValueChange = onPhoneChange) }
        item { FormField(label = "العنوان",              value = form.address, onValueChange = onAddressChange) }
        item { FormField(label = "الدولة",               value = form.country, onValueChange = onCountryChange) }
        item { FormField(label = "الخطة",                value = form.plan,    onValueChange = onPlanChange, hint = "trial | basic | pro | enterprise") }
        item { FormField(label = "تاريخ انتهاء الخطة", value = form.planExpiresAt, onValueChange = onExpiresChange, hint = "YYYY-MM-DD") }
        item { FormField(label = "الحد الأقصى للطلاب", value = form.maxStudents, onValueChange = onMaxStudents) }
        item { FormField(label = "الحد الأقصى للموظفين", value = form.maxStaff, onValueChange = onMaxStaff) }

        if (isCreate) {
            item {
                HorizontalDivider(color = AppTheme.color.border.copy(alpha = .5f))
                Spacer(Modifier.height(4.dp))
                Text(
                    text      = "بيانات المشرف الأول",
                    color     = AppTheme.color.text,
                    style     = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
            item { FormField(label = "اسم المشرف *",       value = form.adminName,     onValueChange = onAdminName,     error = form.adminNameError) }
            item { FormField(label = "بريد المشرف *",       value = form.adminEmail,    onValueChange = onAdminEmail,    error = form.adminEmailError) }
            item { FormField(label = "كلمة مرور المشرف *", value = form.adminPassword, onValueChange = onAdminPassword, error = form.adminPasswordError, isPassword = true) }
        }

        item {
            // Confirm / cancel buttons
            Row(
                modifier              = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppTheme.color.bgHover)
                        .border(1.dp, AppTheme.color.border, RoundedCornerShape(12.dp))
                        .clickAnimation(onDismiss)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("إلغاء", color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
                }

                Box(
                    modifier = Modifier
                        .weight(2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppTheme.color.primary)
                        .clickAnimation(onConfirm)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color    = AppTheme.color.bg,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text("حفظ", color = AppTheme.color.bg,
                            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null,
    hint: String? = null,
    isPassword: Boolean = false,
) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text  = label,
            color = if (error != null) Color(0xFFEF4444) else AppTheme.color.textSecondary,
            style = AppTheme.textStyle.label.small.copy(fontWeight = FontWeight.Medium),
        )
        BasicTextField(
            value         = value,
            onValueChange = onValueChange,
            textStyle     = AppTheme.textStyle.body.small.copy(
                color     = AppTheme.color.text,
                textAlign = TextAlign.End,
            ),
            cursorBrush   = SolidColor(AppTheme.color.primary),
            visualTransformation = if (isPassword)
                androidx.compose.ui.text.input.PasswordVisualTransformation()
            else
                androidx.compose.ui.text.input.VisualTransformation.None,
            modifier      = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (error != null) Color(0xFFEF4444).copy(alpha = .06f)
                    else AppTheme.color.bgHover
                )
                .border(
                    width = 1.dp,
                    color = if (error != null) Color(0xFFEF4444).copy(alpha = .5f)
                    else AppTheme.color.border,
                    shape = RoundedCornerShape(10.dp),
                )
                .padding(12.dp),
            decorationBox = { inner ->
                val placeholder = hint ?: ""
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text  = placeholder,
                        color = AppTheme.color.textSecondary.copy(alpha = .6f),
                        style = AppTheme.textStyle.label.small,
                    )
                }
                inner()
            },
        )
        if (error != null) {
            Text(
                text  = error,
                color = Color(0xFFEF4444),
                style = AppTheme.textStyle.label.small,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Change-plan dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ChangePlanDialog(
    state: UniversitiesUiState,
    listener: UniversitiesInteractionListener,
) {
    val form   = state.changePlanForm
    val target = state.planDialogTarget

    AlertDialog(
        onDismissRequest  = listener::onDismissPlanDialog,
        containerColor    = AppTheme.color.bg,
        title = {
            Text(
                text      = "تغيير الخطة — ${target?.name.orEmpty()}",
                color     = AppTheme.color.text,
                style     = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.End,
                modifier  = Modifier.fillMaxWidth(),
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FormField(
                    label         = "الخطة",
                    value         = form.plan,
                    onValueChange = listener::onPlanDialogPlanChange,
                    hint          = "trial | basic | pro | enterprise",
                )
                FormField(
                    label         = "تاريخ الانتهاء",
                    value         = form.planExpiresAt,
                    onValueChange = listener::onPlanDialogExpiresAtChange,
                    hint          = "YYYY-MM-DD",
                )
                FormField(
                    label         = "الحد الأقصى للطلاب",
                    value         = form.maxStudents,
                    onValueChange = listener::onPlanDialogMaxStudentsChange,
                )
                FormField(
                    label         = "الحد الأقصى للموظفين",
                    value         = form.maxStaff,
                    onValueChange = listener::onPlanDialogMaxStaffChange,
                )
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppTheme.color.primary)
                    .clickAnimation(listener::onConfirmChangePlan)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                if (state.actionLoadingId == target?.id) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color    = AppTheme.color.bg,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("تأكيد", color = AppTheme.color.bg,
                        style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
                }
            }
        },
        dismissButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppTheme.color.bgHover)
                    .clickAnimation(listener::onDismissPlanDialog)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text("إلغاء", color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
            }
        },
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Plan pill (shared between cards)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PlanPill(plan: String) {
    val (bg, fg) = when (plan.lowercase()) {
        "trial"      -> Color(0xFFF3F4F6) to AppTheme.color.textSecondary
        "basic"      -> Color(0xFFEFF6FF) to Color(0xFF3B82F6)
        "pro"        -> AppTheme.color.secondary to AppTheme.color.primary
        "enterprise" -> Color(0xFFF5F3FF) to Color(0xFF7C3AED)
        else         -> Color(0xFFF3F4F6) to AppTheme.color.textSecondary
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text  = plan.replaceFirstChar { it.uppercase() },
            color = fg,
            style = AppTheme.textStyle.label.small.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty / Error states
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UniversitiesEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = "لا توجد جامعات مطابقة للبحث",
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.body.medium,
        )
    }
}

@Composable
private fun UniversitiesErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(message, color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.body.medium, textAlign = TextAlign.Center)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.color.primary)
                    .clickAnimation(onRetry)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text("إعادة المحاولة", color = AppTheme.color.bg,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Effect handler
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HandleEffects(
    effects: Flow<UniversitiesEffect>,
    snackbarHost: SnackbarHostState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onNavigateBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                UniversitiesEffect.NavigateBack            -> onNavigateBack()
                UniversitiesEffect.NavigateToCreateUniversity -> { /* handled by sheet */ }
                UniversitiesEffect.ScrollToTop             -> listState.animateScrollToItem(0)
                is UniversitiesEffect.ShowSnackbar         -> snackbarHost.showSnackbar(effect.message)
            }
        }
    }
}