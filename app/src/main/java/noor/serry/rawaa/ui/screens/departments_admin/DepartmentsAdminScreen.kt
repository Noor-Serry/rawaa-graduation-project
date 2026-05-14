package noor.serry.rawaa.ui.screens.departments_admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.LabelInputField
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R

// ─────────────────────────────────────────────────────────────────────────────
// Entry point
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DepartmentsAdminScreen(
    viewModel: DepartmentsAdminViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // One-time effects (snackbar / navigation) – wire to your host
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { /* ShowSuccess / ShowError */ }
    }

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.color.primary)
            }
        }
        state.errorMessage != null -> {
            DepartmentsErrorState(
                message = state.errorMessage ?: "",
                onRetry = viewModel::load,
            )
        }
        else -> {
            DepartmentsContent(state = state, listener = viewModel)
        }
    }

    // ── Delete confirmation dialog ─────────────────────────────────────────────
    if (state.pendingDeleteId != null) {
        DeleteConfirmDialog(
            onConfirm = viewModel::onDeleteConfirmed,
            onDismiss = viewModel::onDeleteDismissed,
        )
    }

    // ── Create / Edit sheet ────────────────────────────────────────────────────
    if (state.showFormSheet) {
        DepartmentFormSheet(state = state, listener = viewModel)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main content  (LazyColumn, same structure as CoursesAdmin / UsersAdmin)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DepartmentsContent(
    state: DepartmentsAdminUiState,
    listener: DepartmentsAdminInteractionListener,
) {
    // Local search state
    var searchQuery by remember { mutableStateOf("") }
    val displayed = state.departments.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier       = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        // 1 ── Hero header
        item {
            DepartmentsHero(
                total    = state.departments.size,
                onAdd    = listener::onAddDepartmentClicked,
            )
        }

        // 2 ── Search bar
        item {
            DepartmentsSearchBar(
                query     = searchQuery,
                onChanged = { searchQuery = it },
                modifier  = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp, bottom = 10.dp),
            )
        }

        // 3 ── Empty state
        if (displayed.isEmpty()) {
            item {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = if (searchQuery.isBlank()) "لا توجد أقسام" else "لا توجد نتائج",
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.medium,
                    )
                }
            }
        }

        // 4 ── Department cards
        items(displayed, key = { it.id }) { dept ->
            DepartmentCard(
                item     = dept,
                onEdit   = { listener.onEditDepartmentClicked(dept) },
                onDelete = { listener.onDeleteDepartmentClicked(dept.id) },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 10.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero header  (matches CoursesAdmin & UsersAdmin hero style)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DepartmentsHero(
    total: Int,
    onAdd: () -> Unit,
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
        // Title row
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text      = "الأقسام الأكاديمية",
                color     = AppTheme.color.bg,
                style     = AppTheme.textStyle.headline.small,
                modifier  = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
            Text(
                text      = "إدارة أقسام الكلية والتخصصات",
                color     = AppTheme.color.bg.copy(alpha = .75f),
                style     = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                modifier  = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        }

        // Stat chip
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier.fillMaxWidth(),
        ) {
            HeroStatChip(
                count    = total,
                label    = "إجمالي الأقسام",
                modifier = Modifier.weight(1f),
            )
        }

        // Add button
        BaseButton(
            text              = "إضافة قسم جديد",
            onClick           = onAdd,
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
// Search bar  (same style as UsersAdmin search)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DepartmentsSearchBar(
    query: String,
    onChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
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
                        text  = "بحث عن قسم...",
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
// Department card  (matches UserCard layout: shadow, bg, dividers, action row)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DepartmentCard(
    item: DepartmentsAdminUiState.DepartmentItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
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

            // ── Card header ───────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Icon bubble (left / leading for RTL)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppTheme.color.primary.copy(alpha = .10f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter  = painterResource(R.drawable.university),
                        tint     = AppTheme.color.primary,
                        modifier = Modifier.size(20.dp),
                    )
                }

                // Name + sub-info (right-aligned, RTL)
                Column(
                    modifier              = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    horizontalAlignment   = Alignment.End,
                    verticalArrangement   = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text      = item.name,
                        color     = AppTheme.color.text,
                        style     = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                        maxLines  = 1,
                        overflow  = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                    )
                    if (item.coursesCount != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            DeptBadge(
                                label    = "${item.studentsCount ?: 0} طالب",
                                bg       = Color(0xFFEFF6FF),
                                textColor = Color(0xFF1D4ED8),
                            )
                            DeptBadge(
                                label    = "${item.coursesCount} مقررات",
                                bg       = Color(0xFFF3F4F6),
                                textColor = AppTheme.color.textSecondary,
                            )
                        }
                    }
                }
            }

            Divider(color = AppTheme.color.border.copy(alpha = .5f), thickness = 0.8.dp)

            // ── Action buttons: تعديل / حذف ───────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // حذف
                ActionButton(
                    label     = "حذف",
                    iconRes   = R.drawable.ic_trash,
                    textColor = Color(0xFFDC2626),
                    bgColor   = Color(0xFFFEE2E2),
                    onClick   = onDelete,
                )
                // تعديل
                ActionButton(
                    label     = "تعديل",
                    iconRes   = R.drawable.ic_edit,
                    textColor = AppTheme.color.textSecondary,
                    bgColor   = AppTheme.color.bgHover,
                    modifier  = Modifier.weight(1f),
                    onClick   = onEdit,
                )
            }
        }
    }
}

@Composable
private fun DeptBadge(label: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text  = label,
            color = textColor,
            style = AppTheme.textStyle.label.small.copy(fontSize = 10.sp),
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
// Delete confirmation dialog  (identical style to UsersAdmin)
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
                text  = "هل أنت متأكد من حذف هذا القسم؟ لا يمكن التراجع عن هذه العملية.",
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
// Create / Edit form  (full-screen, matches AddUserScreen style)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DepartmentFormSheet(
    state: DepartmentsAdminUiState,
    listener: DepartmentsAdminInteractionListener,
) {
    // Render as a full-screen overlay pushed on the back-stack —
    // same approach as AddUserScreen (Scaffold + top bar).
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bg),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.color.bg)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Back button (leading in RTL)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.color.bgHover)
                        .clickable(onClick = listener::onFormDismissed),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter            = painterResource(R.drawable.ic_arrow_forward),
                        contentDescription = "رجوع",
                        tint               = AppTheme.color.primaryDark,
                        modifier           = Modifier.size(18.dp),
                    )
                }

                Text(
                    text  = if (state.isEditing) "تعديل القسم" else "إضافة قسم جديد",
                    color = AppTheme.color.primaryDark,
                    style = AppTheme.textStyle.headline.small,
                )

                // Spacer to balance the row
                Spacer(Modifier.size(36.dp))
            }

            Divider(color = AppTheme.color.border.copy(alpha = .5f), thickness = 0.8.dp)

            // ── Form body ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                Spacer(Modifier.height(20.dp))

                LabelInputField(
                    text          = state.formName,
                    onValueChange = listener::onFormNameChanged,
                    hintText      = "مثال: علوم الحاسب",
                    label         = "اسم القسم",
                    icon          = painterResource(R.drawable.university),
                    isError       = state.formError != null,
                    errorMessage  = state.formError,
                )

                Spacer(Modifier.height(24.dp))

                // Error banner (API-level)
                if (state.formError != null && state.formError.length > 40) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFEE2E2))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text  = state.formError,
                            color = Color(0xFFDC2626),
                            style = AppTheme.textStyle.body.small,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Submit button
                BaseButton(
                    text              = if (state.isSaving) "جاري الحفظ..." else
                        if (state.isEditing) "حفظ التعديلات" else "إضافة القسم",
                    onClick           = listener::onFormSubmit,
                    roundedCornerSize = 10.dp,
                    isEnable          = !state.isSaving,
                )
                Spacer(Modifier.height(10.dp))

                // Cancel button
                BaseButton(
                    text              = "إلغاء",
                    onClick           = listener::onFormDismissed,
                    roundedCornerSize = 10.dp,
                    backgroundColor   = AppTheme.color.bgHover,
                    textColor         = AppTheme.color.textSecondary,
                    borderColor       = AppTheme.color.border,
                    borderWidth       = 1.dp,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Error state  (identical to CoursesAdmin / UsersAdmin error)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DepartmentsErrorState(message: String, onRetry: () -> Unit) {
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