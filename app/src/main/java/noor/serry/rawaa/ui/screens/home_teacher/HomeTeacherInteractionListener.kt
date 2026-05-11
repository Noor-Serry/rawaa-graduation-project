package noor.serry.rawaa.ui.screens.home_teacher

/**
 * Interaction callbacks for the teacher home screen.
 *
 * Only actions that have a matching backend endpoint or navigation destination
 * are kept:
 *
 *   ✅ onGradingClick()        — grading screen exists (Assessment destination)
 *   ✅ onViewAllCoursesClick() — GET /api/courses + GET /api/doctor/dashboard
 *   ✅ onManageCourseClick()   — GET /api/courses/{id}
 *
 * Removed:
 *   ❌ onAddCourseClick()      — POST /api/courses is admin-only; teacher has no create-course endpoint
 *   ❌ onNewAssignmentClick()  — no assignment-creation endpoint exists for teachers
 *   ❌ onReportsClick()        — report endpoints are admin-only (GET /api/admin/reports/*)
 *   ❌ onViewAllScheduleClick()— schedule data is embedded in the dashboard; no standalone teacher schedule screen
*/
 * */
interface HomeTeacherInteractionListener {
fun onGradingClick()
fun onViewAllCoursesClick()
fun onManageCourseClick(courseId: Int)
}