package noor.serry.rawaa.ui.screens.users_admin

import noor.serry.rawaa.data.dto.DepartmentDto

// ── Shared data-layer → UI-layer mappers ──────────────────────────────────────

internal fun DepartmentDto.toDepartmentFilterItem() =
    UsersAdminUiState.DepartmentFilterItem(id = id, name = name)
