package noor.serry.rawaa.ui.screens.courses_student

interface CoursesInteractionListener {
    fun onTabSelected(tab: CoursesTab)
    fun onEnrollClick(courseId : Int)
    // Removed: onLecturesClick  — no /lectures endpoint on the server
    // Removed: onHomeworkClick  — no /homework endpoint on the server
    // Removed: onMaterialsClick — no /materials endpoint on the server
}
