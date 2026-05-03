package noor.serry.rawaa.ui.screens.profile_student

interface ProfileInteractionListener {
    fun onEditProfileClick()
    fun onChangeAvatarClick()
    fun onEditFieldClick(field: ProfileField)
    fun onAchievementsClick()
    fun onCertificatesClick()
}
