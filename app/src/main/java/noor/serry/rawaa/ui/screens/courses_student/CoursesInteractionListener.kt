package noor.serry.rawaa.ui.screens.courses_student

interface CoursesInteractionListener {
    fun onTabSelected(tab: CoursesTab)
    fun onOpenCourse(courseCode: String)
    fun onLecturesClick(courseCode: String)
    fun onHomeworkClick(courseCode: String)
    fun onMaterialsClick(courseCode: String)
    fun onEnrollClick(courseCode: String)
}
