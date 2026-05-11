package noor.serry.rawaa.ui.screens.profile_student

interface ProfileInteractionListener {
    fun onEditProfileClick()
    fun onChangeAvatarClick()
    fun onEditFieldClick(field: ProfileField)
    // Removed: onAchievementsClick — no achievements endpoint on server
    // Removed: onCertificatesClick — no certificates endpoint on server
}
