package noor.serry.rawaa.ui.screens.register.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.LabelInputField
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.data.dto.DepartmentDto
import noor.serry.rawaa.ui.screens.login.component.UserRoleCard
import noor.serry.rawaa.ui.screens.register.UserRole

@Composable
fun AcademicInfoPage(
    university: String,
    selectedRole: UserRole,
    departments: List<DepartmentDto>,
    isDepartmentsLoading: Boolean,
    selectedDepartment: DepartmentDto?,
    departmentError: String?,
    roleTitle: String,
    roleTitleError: String?,
    onUniversityChange: (String) -> Unit,
    onRoleSelected: (UserRole) -> Unit,
    onDepartmentSelected: (DepartmentDto) -> Unit,
    onRoleTitleChange: (String) -> Unit,
    onNext: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.academic_info),
            color = AppTheme.color.text,
            style = AppTheme.textStyle.headline.small.copy(fontSize = 20.sp),
        )
        Text(
            text = stringResource(R.string.step_2_of_3),
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(top = 4.dp)
        )

        LabelInputField(
            text = university,
            onValueChange = onUniversityChange,
            hintText = "",
            label = stringResource(R.string.university),
            icon = painterResource(R.drawable.university),
            modifier = Modifier.padding(top = 24.dp)
        )

        when {
            isDepartmentsLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            departments.isNotEmpty() -> DepartmentGrid(
                departments = departments,
                selected = selectedDepartment,
                error = departmentError,
                onSelect = onDepartmentSelected,
            )
            departmentError != null -> Text(
                text = departmentError,
                color = AppTheme.color.error,
                style = AppTheme.textStyle.body.small,
            )
        }



        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                stringResource(R.string.job_role),
                color = AppTheme.color.primaryDark,
                style = AppTheme.textStyle.body.small
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UserRoleCard(
                    isSelected = selectedRole == UserRole.STUDENT,
                    label = stringResource(R.string.student),
                    onClick = { onRoleSelected(UserRole.STUDENT) },
                    modifier = Modifier.height(96.dp)
                )
                UserRoleCard(
                    isSelected = selectedRole == UserRole.TEACHER,
                    label = stringResource(R.string.teacher),
                    onClick = { onRoleSelected(UserRole.TEACHER) },
                    modifier = Modifier.height(96.dp)
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UserRoleCard(
                    isSelected = selectedRole == UserRole.STAKEHOLDER,
                    label = stringResource(R.string.stakeholder),
                    onClick = { onRoleSelected(UserRole.STAKEHOLDER) },
                    modifier = Modifier.height(96.dp)
                )
                UserRoleCard(
                    isSelected = selectedRole == UserRole.ADMIN,
                    label = stringResource(R.string.admin),
                    onClick = { onRoleSelected(UserRole.ADMIN) },
                    modifier = Modifier.height(96.dp)
                )
            }
        }
        if (selectedRole == UserRole.TEACHER) {
            LabelInputField(
                text = roleTitle,
                onValueChange = onRoleTitleChange,
                label = "اللقب الوظيفي",
                hintText = "استاذ مساعد",
                isError = roleTitleError != null,
                errorMessage = roleTitleError,
            )
        }
        BaseButton(
            text = stringResource(R.string.next),
            onClick = onNext,
            modifier = Modifier.padding(top = 32.dp),
            roundedCornerSize = 8.dp
        )
    }
}


@Composable
private fun DepartmentGrid(
    departments: List<DepartmentDto>,
    selected: DepartmentDto?,
    error: String?,
    onSelect: (DepartmentDto) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("اختر القسم", style = AppTheme.textStyle.body.medium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            departments.forEach { dept ->
                val isSelected = dept.id == selected?.id
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelect(dept) },
                    label = { Text(dept.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppTheme.color.primary,
                        selectedLabelColor = Color.White,
                    )
                )
            }
        }
        if (error != null) {
            Text(error, color = AppTheme.color.error, style = AppTheme.textStyle.body.small)
        }
    }
}