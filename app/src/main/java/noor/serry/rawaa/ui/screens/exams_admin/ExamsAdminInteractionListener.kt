package noor.serry.rawaa.ui.screens.exams_admin

interface ExamsAdminInteractionListener {
    fun onExamClicked(examId: Int)
    fun onAddExamClicked()
    fun onPublishExam(examId: Int)
    // Create form
    fun onFormCourseSelected(courseId: Int?)
    fun onFormTitleChanged(title: String)
    fun onFormTypeSelected(type: String)
    fun onFormTotalMarksChanged(marks: String)
    fun onFormDurationChanged(duration: String)
    fun onFormStartAtChanged(startAt: String)
    fun onFormEndAtChanged(endAt: String)
    fun onFormSubmit()
    fun onFormDismissed()
}
