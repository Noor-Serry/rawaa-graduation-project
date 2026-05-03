package noor.serry.rawaa.ui.screens.students

sealed interface StudentsEffect {
    data class NavigateToStudentProfile(val studentId: String) : StudentsEffect
    data class NavigateToSendMessage(val studentId: String) : StudentsEffect
    data object NavigateToSendBulkMessage : StudentsEffect
}