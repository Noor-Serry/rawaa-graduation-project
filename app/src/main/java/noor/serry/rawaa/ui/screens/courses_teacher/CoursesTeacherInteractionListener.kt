package noor.serry.rawaa.ui.screens.courses_teacher

interface CoursesTeacherInteractionListener {
    fun onTabSelected(tab: CoursesTeacherTab)
    fun onManageCourseClick(courseId: String)
    fun onAddCourseClick()
    fun load()
}