package noor.serry.rawaa.ui.screens.grading_teacher

/**
 * Interaction contract for the grading screen.
 *
 * Only actions that have a corresponding backend endpoint or are
 * pure client-side operations are listed here.
 *
 * Intentionally omitted (no backend endpoint):
 *   • onStartGradingClick  — no assignment-submission grading endpoint
 *   • onViewDetailsClick   — no course-detail grading endpoint
 *   • onViewStatsClick     — no per-course grading stats endpoint
 */
interface GradingInteractionListener {
    fun onTabSelected(tab: GradingTab)
    fun onSearchChange(query: String)
    fun onRetry()
}
