package noor.serry.rawaa.ui.screens.profile_student

interface ProfileInteractionListener {
    fun onEditProfileClick()        // enter edit mode
    fun onCancelEditClick()         // exit edit mode without saving
    fun onSaveProfileClick()        // PUT /api/auth/profile + PUT /api/students/{id}
    fun onChangeAvatarClick()       // open image picker

    fun onNameChanged(value: String)
    fun onPhoneChanged(value: String)
    fun onEmailChanged(value: String)
}
