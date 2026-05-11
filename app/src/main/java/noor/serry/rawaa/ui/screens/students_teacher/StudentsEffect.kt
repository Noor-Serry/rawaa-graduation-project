package noor.serry.rawaa.ui.screens.students_teacher

sealed interface StudentsEffect {
    data class NavigateToStudentProfile(val studentId: String) : StudentsEffect
    // Removed: NavigateToSendMessage — no per-student messaging endpoint in UniversityRepository
    // Removed: NavigateToSendBulkMessage — no bulk messaging endpoint in UniversityRepository
}