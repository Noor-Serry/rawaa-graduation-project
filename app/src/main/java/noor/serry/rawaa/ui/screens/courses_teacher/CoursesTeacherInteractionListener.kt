package noor.serry.rawaa.ui.screens.courses_teacher

interface CoursesTeacherInteractionListener {
    fun onTabSelected(tab: CoursesTeacherTab)
    // onManageCourseClick uses Int to match CourseDto.id; no add-course action exposed
    // because there is no doctor-facing POST /api/courses endpoint.
    fun onCourseClick(courseId: Int)
    fun load()
}