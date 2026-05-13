package noor.serry.rawaa.ui.screens.schedules_admin

sealed interface SchedulesAdminEffect {
    data class ShowSuccess(val message: String) : SchedulesAdminEffect
    data class ShowError(val message: String)   : SchedulesAdminEffect
}
