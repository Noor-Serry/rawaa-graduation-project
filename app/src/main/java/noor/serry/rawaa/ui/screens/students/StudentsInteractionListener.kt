package noor.serry.rawaa.ui.screens.students

interface StudentsInteractionListener {
    fun onSearchChange(query: String)
    fun onViewProfileClick(studentId: String)
    fun onSendMessageClick(studentId: String)
    fun onSendBulkMessageClick()
}