package noor.serry.rawaa.ui.screens.home_student

interface HomeStudentInteractionListener {
    fun onViewAllSchedule()
    fun onViewAllCourses()
    fun onMyCoursesClick()
    fun onScheduleClick()
    // Removed: onGradesClick — no dedicated grades screen or nav route exists for students
    // Removed: onViewAllHomework, onHomeworkClick — no homework endpoint on server
}
