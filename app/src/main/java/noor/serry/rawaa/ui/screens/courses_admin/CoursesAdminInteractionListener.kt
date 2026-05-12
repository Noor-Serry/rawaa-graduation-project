package noor.serry.rawaa.ui.screens.courses_admin

interface CoursesAdminInteractionListener {
    fun onTabSelected(tab: CoursesAdminUiState.CourseAdminTab)
    fun onDepartmentFilterSelected(departmentId: Int?)
    fun onCourseClicked(courseId: Int)
}
