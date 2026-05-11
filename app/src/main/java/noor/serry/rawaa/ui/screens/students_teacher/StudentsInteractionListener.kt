package noor.serry.rawaa.ui.screens.students_teacher

interface StudentsInteractionListener {
    fun onSearchChange(query: String)
    fun onViewProfileClick(studentId: String)
    // Removed: onSendMessageClick — no per-student messaging endpoint in UniversityRepository
    // Removed: onSendBulkMessageClick — no bulk messaging endpoint in UniversityRepository
}