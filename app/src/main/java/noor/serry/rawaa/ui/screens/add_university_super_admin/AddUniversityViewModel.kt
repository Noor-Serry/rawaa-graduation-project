package noor.serry.rawaa.ui.screens.add_university_super_admin

import noor.serry.rawaa.data.dto.CreateUniversityRequest
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class AddUniversityViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<AddUniversityUiState, AddUniversityEffect>(
    initialState       = AddUniversityUiState(),
    dispatcherProvider = dispatchers,
), AddUniversityInteractionListener {

    // ── Navigation ────────────────────────────────────────────────────────────

    override fun onBackClick() =
        sendNewNavigationEffect(AddUniversityEffect.NavigateBack)

    // ── University info ───────────────────────────────────────────────────────

    override fun onNameChange(value: String) {
        updateState {
            it.copy(
                name      = value,
                nameError = if (value.isBlank()) "الاسم مطلوب" else null,
            )
        }
    }

    override fun onNameEnChange(value: String) =
        updateState { it.copy(nameEn = value) }

    override fun onSlugChange(value: String) {
        val sanitised = value.lowercase().replace(" ", "-")
        updateState {
            it.copy(
                slug      = sanitised,
                slugError = when {
                    sanitised.isBlank()                         -> "المعرّف مطلوب"
                    !sanitised.matches(Regex("^[a-z0-9-]+$"))  -> "يجب أن يحتوي على حروف إنجليزية صغيرة وأرقام وشرطات فقط"
                    else                                        -> null
                },
            )
        }
    }

    override fun onEmailChange(value: String) {
        updateState {
            it.copy(
                email      = value,
                emailError = if (value.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches())
                    "البريد الإلكتروني غير صحيح" else null,
            )
        }
    }

    override fun onPhoneChange(value: String) =
        updateState { it.copy(phone = value) }

    override fun onAddressChange(value: String) =
        updateState { it.copy(address = value) }

    override fun onCountryChange(value: String) =
        updateState { it.copy(country = value) }

    // ── Plan ──────────────────────────────────────────────────────────────────

    override fun onPlanChange(value: String) =
        updateState { it.copy(plan = value) }

    override fun onPlanExpiresAtChange(value: String) =
        updateState { it.copy(planExpiresAt = value) }

    override fun onMaxStudentsChange(value: String) =
        updateState { it.copy(maxStudents = value.filter { c -> c.isDigit() }) }

    override fun onMaxStaffChange(value: String) =
        updateState { it.copy(maxStaff = value.filter { c -> c.isDigit() }) }

    // ── First admin ───────────────────────────────────────────────────────────

    override fun onAdminNameChange(value: String) {
        updateState {
            it.copy(
                adminName      = value,
                adminNameError = if (value.isBlank()) "اسم المسؤول مطلوب" else null,
            )
        }
    }

    override fun onAdminEmailChange(value: String) {
        updateState {
            it.copy(
                adminEmail      = value,
                adminEmailError = when {
                    value.isBlank()                                                              -> "البريد الإلكتروني مطلوب"
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()               -> "البريد الإلكتروني غير صحيح"
                    else                                                                         -> null
                },
            )
        }
    }

    override fun onAdminPasswordChange(value: String) {
        updateState {
            it.copy(
                adminPassword      = value,
                adminPasswordError = when {
                    value.isBlank()   -> "كلمة المرور مطلوبة"
                    value.length < 8  -> "يجب أن تكون 8 أحرف على الأقل"
                    else              -> null
                },
            )
        }
    }

    override fun onTogglePasswordVisibility() =
        updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    // ── Submit ────────────────────────────────────────────────────────────────

    override fun onSubmit() {
        // Run a full validation pass first so every error label appears at once
        val s = state.value
        val nameErr          = if (s.name.isBlank()) "الاسم مطلوب" else null
        val slugErr          = if (s.slug.isBlank()) "المعرّف مطلوب" else s.slugError
        val emailErr         = s.emailError
        val adminNameErr     = if (s.adminName.isBlank()) "اسم المسؤول مطلوب" else null
        val adminEmailErr    = when {
            s.adminEmail.isBlank()                                                    -> "البريد الإلكتروني مطلوب"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(s.adminEmail).matches()      -> "البريد الإلكتروني غير صحيح"
            else                                                                       -> null
        }
        val adminPasswordErr = when {
            s.adminPassword.isBlank()   -> "كلمة المرور مطلوبة"
            s.adminPassword.length < 8  -> "يجب أن تكون 8 أحرف على الأقل"
            else                        -> null
        }

        val hasErrors = listOf(
            nameErr, slugErr, emailErr, adminNameErr, adminEmailErr, adminPasswordErr,
        ).any { it != null }

        if (hasErrors) {
            updateState {
                it.copy(
                    nameError          = nameErr,
                    slugError          = slugErr,
                    emailError         = emailErr,
                    adminNameError     = adminNameErr,
                    adminEmailError    = adminEmailErr,
                    adminPasswordError = adminPasswordErr,
                )
            }
            return
        }

        updateState { it.copy(isSubmitting = true) }

        tryToExecute(
            action = {
                repository.createUniversity(
                    CreateUniversityRequest(
                        name          = s.name,
                        nameEn        = s.nameEn.takeIf { it.isNotBlank() },
                        slug          = s.slug,
                        email         = s.email.takeIf { it.isNotBlank() },
                        phone         = s.phone.takeIf { it.isNotBlank() },
                        address       = s.address.takeIf { it.isNotBlank() },
                        country       = s.country.takeIf { it.isNotBlank() },
                        plan          = s.plan,
                        planExpiresAt = s.planExpiresAt.takeIf { it.isNotBlank() },
                        maxStudents   = s.maxStudents.toIntOrNull() ?: 500,
                        maxStaff      = s.maxStaff.toIntOrNull()    ?: 50,
                        adminName     = s.adminName,
                        adminEmail    = s.adminEmail,
                        adminPassword = s.adminPassword,
                    )
                )
            },
            onSuccess = {
                updateState { it.copy(isSubmitting = false) }
                sendNewEffect(AddUniversityEffect.ShowSnackbar("تمت إضافة الجامعة بنجاح"))
                sendNewNavigationEffect(AddUniversityEffect.NavigateBackAfterCreate)
            },
            onError = { e ->
                updateState { it.copy(isSubmitting = false) }
                sendNewEffect(AddUniversityEffect.ShowSnackbar(e.message ?: "حدث خطأ غير متوقع"))
            },
            dispatcher = dispatchers.IO,
        )
    }
}
