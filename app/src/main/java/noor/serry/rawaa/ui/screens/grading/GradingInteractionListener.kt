package noor.serry.rawaa.ui.screens.grading

interface GradingInteractionListener {
    fun onTabSelected(tab: GradingTab)
    fun onSearchChange(query: String)
    fun onStartGradingClick(assignmentId: String)
    fun onViewDetailsClick(assignmentId: String)
    fun onViewStatsClick(assignmentId: String)

}