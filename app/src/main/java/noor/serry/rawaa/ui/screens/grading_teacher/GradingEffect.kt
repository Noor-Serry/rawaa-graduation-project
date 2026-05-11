package noor.serry.rawaa.ui.screens.grading_teacher

sealed interface GradingEffect {
    data class ShowError(val message: String) : GradingEffect
}
