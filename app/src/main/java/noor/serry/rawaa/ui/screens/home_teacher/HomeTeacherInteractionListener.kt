package noor.serry.rawaa.ui.screens.home_teacher

interface HomeTeacherInteractionListener {
    fun onAddCourseClick()
    fun onNewAssignmentClick()
    fun onGradingClick()
    fun onReportsClick()
    fun onViewAllScheduleClick()
    fun onViewAllCoursesClick()
    fun onManageCourseClick(courseId: String)
}