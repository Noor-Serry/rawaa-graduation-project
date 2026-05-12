package noor.serry.rawaa.ui.screens.courses_admin

data class CoursesAdminUiState(
    val selectedTab: CourseAdminTab = CourseAdminTab.ACTIVE,
    val activeCourses: List<CourseAdminItem> = emptyList(),
    val inactiveCourses: List<CourseAdminItem> = emptyList(),
    val departments: List<DeptFilterItem> = emptyList(),
    val selectedDepartmentId: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    // From CourseDto
    data class CourseAdminItem(
        val id: Int,
        val name: String,
        val code: String,
        val creditHours: Int,
        val departmentName: String?,
        val doctorName: String?,
        val semester: String?,
        val academicYear: Int?,
        val maxStudents: Int,
        val enrolledCount: Int,
        val isActive: Boolean,
    )

    data class DeptFilterItem(
        val id: Int,
        val name: String,
    )

    enum class CourseAdminTab { ACTIVE, INACTIVE }
}
