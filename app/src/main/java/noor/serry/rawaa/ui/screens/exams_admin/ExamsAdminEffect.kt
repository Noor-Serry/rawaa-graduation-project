package noor.serry.rawaa.ui.screens.exams_admin

sealed interface ExamsAdminEffect {
    data class NavigateToExamDetail(val examId: Int) : ExamsAdminEffect
    data class ShowSuccess(val message: String)      : ExamsAdminEffect
    data class ShowError(val message: String)        : ExamsAdminEffect
}
