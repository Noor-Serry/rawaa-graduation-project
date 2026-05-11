package noor.serry.rawaa.ui.screens.home_teacher

/**
 * One-shot navigation / UI events emitted by HomeTeacherViewModel.
 *
 * Only effects that have a matching backend endpoint or a real navigation
 * destination are kept here:
 *   • NavigateToCourses   – teacher can view/manage courses (GET /api/courses, GET /api/doctor/dashboard)
 *   • NavigateToGrading   – grading screen exists in the Assessment destination
 *   • NavigateToCourseDetail – drills into a specific course (GET /api/courses/{id})
 *   • ShowError           – generic error feedback
 *
 * Removed effects that had no backend or navigation target:
 *   • NavigateToStudents  – no dedicated teacher-student list endpoint
 *   • NavigateToSchedule  – schedule is embedded in the dashboard response
 *   • NavigateToReports   – report endpoints are admin-only (GET /api/admin/reports/*)
 *   • NavigateToAddCourse – creating a course is admin-scoped (POST /api/courses requires admin)
**/
  */
sealed interface HomeTeacherEffect {
data object NavigateToCourses : HomeTeacherEffect
data object NavigateToGrading : HomeTeacherEffect
data class NavigateToCourseDetail(val courseId: Int) : HomeTeacherEffect
data class ShowError(val message: String) : HomeTeacherEffect
}