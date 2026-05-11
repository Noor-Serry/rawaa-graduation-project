package noor.serry.rawaa.ui.navigation.teatcher

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import noor.serry.rawaa.ui.navigation.student.StudentRouteKeys

@Serializable
sealed interface TeacherRouteKeys : NavKey {
    @Serializable
    data object Notifications : TeacherRouteKeys
    @Serializable
    data object Home : TeacherRouteKeys
    @Serializable
    data object Courses : TeacherRouteKeys
    @Serializable
    data object Assessment : TeacherRouteKeys
    @Serializable
    data object Students : TeacherRouteKeys
    @Serializable
    data object Profile : TeacherRouteKeys
    @Serializable
    data class StudentProfile(val studentId: Int) : TeacherRouteKeys
}