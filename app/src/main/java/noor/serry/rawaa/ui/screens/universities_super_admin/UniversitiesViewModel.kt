package noor.serry.rawaa.ui.screens.universities_super_admin
import noor.serry.rawaa.data.dto.ChangePlanRequest
import noor.serry.rawaa.data.dto.CreateUniversityRequest
import noor.serry.rawaa.data.dto.UniversityAdminDto
import noor.serry.rawaa.data.dto.UniversityDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class UniversitiesViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<UniversitiesUiState, UniversitiesEffect>(
    initialState = UniversitiesUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), UniversitiesInteractionListener {

    init { loadPage(page = 1, resetList = true) }

    // ── Core list loader ──────────────────────────────────────────────────────

    private fun loadPage(page: Int, resetList: Boolean) {
        if (resetList) {
            updateState { it.copy(isLoading = true, errorMessage = null) }
        } else {
            updateState { it.copy(isLoadingMore = true) }
        }
        tryToExecute(
            action = {
                val s = state.value  // read AFTER updateState so filters are current
                repository.getSuperAdminUniversities(
                    page     = page,
                    perPage  = 20,
                    plan     = s.filterPlan,
                    isActive = s.filterIsActive,
                    search   = s.searchQuery.takeIf { it.isNotBlank() },
                )
            },
            onSuccess = { resp ->
                val newItems = resp.data.map { it.toItem() }
                updateState { current ->
                    current.copy(
                        isLoading     = false,
                        isLoadingMore = false,
                        universities  = if (resetList) newItems else current.universities + newItems,
                        currentPage   = resp.pagination?.currentPage ?: page,
                        lastPage      = resp.pagination?.lastPage    ?: page,
                        totalCount    = resp.pagination?.total       ?: newItems.size,
                    )
                }
            },
            onError = { e ->
                updateState {
                    it.copy(
                        isLoading     = false,
                        isLoadingMore = false,
                        errorMessage  = e.message,
                    )
                }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Load detail + admins ──────────────────────────────────────────────────

    private fun loadDetail(universityId: Int) {
        updateState { it.copy(isDetailLoading = true, isAdminsLoading = true) }
        tryToExecute(
            action = {
                val detail = repository.getSuperAdminUniversity(universityId)
                val admins = repository.getUniversityAdmins(universityId)
                detail to admins
            },
            onSuccess = { (detailResp, adminsResp) ->
                val item   = detailResp.data?.toItem()
                val admins = adminsResp.data?.map { it.toAdminItem() } ?: emptyList()
                updateState { current ->
                    current.copy(
                        isDetailLoading = false,
                        isAdminsLoading = false,
                        selectedUniversity = item?.let {
                            UniversitiesUiState.UniversityDetailItem(base = it, admins = admins)
                        },
                        admins = admins,
                    )
                }
            },
            onError = { e ->
                updateState {
                    it.copy(
                        isDetailLoading = false,
                        isAdminsLoading = false,
                        errorMessage    = e.message,
                    )
                }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    override fun onBackClick() =
        sendNewNavigationEffect(UniversitiesEffect.NavigateBack)

    // ── Filters & search ──────────────────────────────────────────────────────

    override fun onSearchQueryChange(query: String) {
        updateState { it.copy(searchQuery = query) }
        loadPage(page = 1, resetList = true)
        sendEffect(UniversitiesEffect.ScrollToTop)
    }

    override fun onPlanFilterChange(plan: String?) {
        updateState { it.copy(filterPlan = plan) }
        loadPage(page = 1, resetList = true)
        sendEffect(UniversitiesEffect.ScrollToTop)
    }

    override fun onActiveFilterChange(isActive: Int?) {
        updateState { it.copy(filterIsActive = isActive) }
        loadPage(page = 1, resetList = true)
        sendEffect(UniversitiesEffect.ScrollToTop)
    }

    override fun onClearFilters() {
        updateState {
            it.copy(
                searchQuery    = "",
                filterPlan     = null,
                filterIsActive = null,
            )
        }
        loadPage(page = 1, resetList = true)
        sendEffect(UniversitiesEffect.ScrollToTop)
    }

    // ── Pagination ────────────────────────────────────────────────────────────

    override fun onLoadMore() {
        val s = state.value
        if (!s.canLoadMore || s.isLoadingMore) return
        loadPage(page = s.currentPage + 1, resetList = false)
    }

    override fun onRetry() = loadPage(page = 1, resetList = true)

    // ── Detail panel ──────────────────────────────────────────────────────────

    override fun onUniversityClick(universityId: Int) {
        val alreadySelected = state.value.selectedUniversity?.base?.id == universityId
        if (alreadySelected) {
            onDismissDetailPanel()
        } else {
            loadDetail(universityId)
        }
    }

    override fun onDismissDetailPanel() {
        updateState {
            it.copy(
                selectedUniversity = null,
                admins             = emptyList(),
            )
        }
    }

    // ── Create ────────────────────────────────────────────────────────────────

    override fun onCreateUniversityClick() {
        updateState { it.copy(showCreateSheet = true) }
    }

    // — Form field changes (create)
    override fun onCreateFormNameChange(value: String)          = patchCreate { it.copy(name = value, nameError = null) }
    override fun onCreateFormNameEnChange(value: String)        = patchCreate { it.copy(nameEn = value) }
    override fun onCreateFormSlugChange(value: String)          = patchCreate { it.copy(slug = value, slugError = null) }
    override fun onCreateFormEmailChange(value: String)         = patchCreate { it.copy(email = value) }
    override fun onCreateFormPhoneChange(value: String)         = patchCreate { it.copy(phone = value) }
    override fun onCreateFormAddressChange(value: String)       = patchCreate { it.copy(address = value) }
    override fun onCreateFormCountryChange(value: String)       = patchCreate { it.copy(country = value) }
    override fun onCreateFormPlanChange(value: String)          = patchCreate { it.copy(plan = value) }
    override fun onCreateFormPlanExpiresAtChange(value: String) = patchCreate { it.copy(planExpiresAt = value) }
    override fun onCreateFormMaxStudentsChange(value: String)   = patchCreate { it.copy(maxStudents = value) }
    override fun onCreateFormMaxStaffChange(value: String)      = patchCreate { it.copy(maxStaff = value) }
    override fun onCreateFormAdminNameChange(value: String)     = patchCreate { it.copy(adminName = value, adminNameError = null) }
    override fun onCreateFormAdminEmailChange(value: String)    = patchCreate { it.copy(adminEmail = value, adminEmailError = null) }
    override fun onCreateFormAdminPasswordChange(value: String) = patchCreate { it.copy(adminPassword = value, adminPasswordError = null) }

    private fun patchCreate(block: (UniversitiesUiState.UniversityFormState) -> UniversitiesUiState.UniversityFormState) {
        updateState { it.copy(createForm = block(it.createForm)) }
    }

    override fun onConfirmCreate() {
        val form = state.value.createForm
        var valid = true
        var updated = form

        if (form.name.isBlank()) { updated = updated.copy(nameError = "اسم الجامعة مطلوب"); valid = false }
        if (form.slug.isBlank()) { updated = updated.copy(slugError = "الرابط المختصر مطلوب"); valid = false }
        if (form.adminName.isBlank()) { updated = updated.copy(adminNameError = "اسم المشرف مطلوب"); valid = false }
        if (form.adminEmail.isBlank()) { updated = updated.copy(adminEmailError = "البريد الإلكتروني مطلوب"); valid = false }
        if (form.adminPassword.length < 6) { updated = updated.copy(adminPasswordError = "كلمة المرور 6 أحرف على الأقل"); valid = false }

        if (!valid) { updateState { it.copy(createForm = updated) }; return }

        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = {
                repository.createUniversity(
                    CreateUniversityRequest(
                        name           = form.name,
                        nameEn         = form.nameEn.takeIf { it.isNotBlank() },
                        slug           = form.slug,
                        email          = form.email.takeIf { it.isNotBlank() },
                        phone          = form.phone.takeIf { it.isNotBlank() },
                        address        = form.address.takeIf { it.isNotBlank() },
                        country        = form.country.takeIf { it.isNotBlank() },
                        plan           = form.plan,
                        planExpiresAt  = form.planExpiresAt.takeIf { it.isNotBlank() },
                        maxStudents    = form.maxStudents.toIntOrNull() ?: 500,
                        maxStaff       = form.maxStaff.toIntOrNull()   ?: 50,
                        adminName      = form.adminName,
                        adminEmail     = form.adminEmail,
                        adminPassword  = form.adminPassword,
                    )
                )
            },
            onSuccess = {
                updateState {
                    it.copy(
                        isLoading       = false,
                        showCreateSheet = false,
                        createForm      = UniversitiesUiState.UniversityFormState(),
                    )
                }
                sendEffect(UniversitiesEffect.ShowSnackbar("تم إنشاء الجامعة بنجاح"))
                loadPage(page = 1, resetList = true)
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onDismissCreateSheet() {
        updateState {
            it.copy(
                showCreateSheet = false,
                createForm      = UniversitiesUiState.UniversityFormState(),
            )
        }
    }

    // ── Edit ──────────────────────────────────────────────────────────────────

    override fun onEditUniversityClick(universityId: Int) {
        val item = state.value.universities.find { it.id == universityId } ?: return
        updateState {
            it.copy(
                showEditSheet = true,
                editTarget    = item,
                editForm      = UniversitiesUiState.UniversityFormState(
                    name          = item.name,
                    nameEn        = item.nameEn   ?: "",
                    slug          = item.slug,
                    email         = item.email    ?: "",
                    phone         = item.phone    ?: "",
                    address       = item.address  ?: "",
                    country       = item.country  ?: "",
                    plan          = item.plan     ?: "trial",
                    planExpiresAt = item.planExpiresAt ?: "",
                    maxStudents   = item.maxStudents.toString(),
                    maxStaff      = item.maxStaff.toString(),
                ),
            )
        }
    }

    // — Form field changes (edit)
    override fun onEditFormNameChange(value: String)          = patchEdit { it.copy(name = value, nameError = null) }
    override fun onEditFormNameEnChange(value: String)        = patchEdit { it.copy(nameEn = value) }
    override fun onEditFormSlugChange(value: String)          = patchEdit { it.copy(slug = value, slugError = null) }
    override fun onEditFormEmailChange(value: String)         = patchEdit { it.copy(email = value) }
    override fun onEditFormPhoneChange(value: String)         = patchEdit { it.copy(phone = value) }
    override fun onEditFormAddressChange(value: String)       = patchEdit { it.copy(address = value) }
    override fun onEditFormCountryChange(value: String)       = patchEdit { it.copy(country = value) }
    override fun onEditFormPlanChange(value: String)          = patchEdit { it.copy(plan = value) }
    override fun onEditFormPlanExpiresAtChange(value: String) = patchEdit { it.copy(planExpiresAt = value) }
    override fun onEditFormMaxStudentsChange(value: String)   = patchEdit { it.copy(maxStudents = value) }
    override fun onEditFormMaxStaffChange(value: String)      = patchEdit { it.copy(maxStaff = value) }

    private fun patchEdit(block: (UniversitiesUiState.UniversityFormState) -> UniversitiesUiState.UniversityFormState) {
        updateState { it.copy(editForm = block(it.editForm)) }
    }

    override fun onConfirmEdit() {
        val form   = state.value.editForm
        val target = state.value.editTarget ?: return
        var valid   = true
        var updated = form

        if (form.name.isBlank()) { updated = updated.copy(nameError = "اسم الجامعة مطلوب"); valid = false }
        if (form.slug.isBlank()) { updated = updated.copy(slugError = "الرابط المختصر مطلوب"); valid = false }

        if (!valid) { updateState { it.copy(editForm = updated) }; return }

        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = {
                repository.updateUniversity(
                    id      = target.id,
                    request = CreateUniversityRequest(
                        name          = form.name,
                        nameEn        = form.nameEn.takeIf { it.isNotBlank() },
                        slug          = form.slug,
                        email         = form.email.takeIf { it.isNotBlank() },
                        phone         = form.phone.takeIf { it.isNotBlank() },
                        address       = form.address.takeIf { it.isNotBlank() },
                        country       = form.country.takeIf { it.isNotBlank() },
                        plan          = form.plan,
                        planExpiresAt = form.planExpiresAt.takeIf { it.isNotBlank() },
                        maxStudents   = form.maxStudents.toIntOrNull() ?: 500,
                        maxStaff      = form.maxStaff.toIntOrNull()   ?: 50,
                        // edit does not change admin credentials
                        adminName     = "",
                        adminEmail    = "",
                        adminPassword = "",
                    ),
                )
            },
            onSuccess = {
                updateState {
                    it.copy(
                        isLoading     = false,
                        showEditSheet = false,
                        editTarget    = null,
                        editForm      = UniversitiesUiState.UniversityFormState(),
                    )
                }
                sendEffect(UniversitiesEffect.ShowSnackbar("تم تحديث بيانات الجامعة"))
                loadPage(page = 1, resetList = true)
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onDismissEditSheet() {
        updateState {
            it.copy(
                showEditSheet = false,
                editTarget    = null,
                editForm      = UniversitiesUiState.UniversityFormState(),
            )
        }
    }

    // ── Activate / Deactivate ─────────────────────────────────────────────────

    override fun onActivateUniversity(universityId: Int) {
        updateState { it.copy(actionLoadingId = universityId) }
        tryToExecute(
            action    = { repository.activateUniversity(universityId) },
            onSuccess = {
                updateState { s ->
                    s.copy(
                        actionLoadingId = null,
                        universities    = s.universities.map { u ->
                            if (u.id == universityId) u.copy(isActive = 1) else u
                        },
                    )
                }
                sendEffect(UniversitiesEffect.ShowSnackbar("تم تفعيل الجامعة"))
            },
            onError = { e ->
                updateState { it.copy(actionLoadingId = null, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onDeactivateUniversity(universityId: Int) {
        updateState { it.copy(actionLoadingId = universityId) }
        tryToExecute(
            action    = { repository.deactivateUniversity(universityId) },
            onSuccess = {
                updateState { s ->
                    s.copy(
                        actionLoadingId = null,
                        universities    = s.universities.map { u ->
                            if (u.id == universityId) u.copy(isActive = 0) else u
                        },
                    )
                }
                sendEffect(UniversitiesEffect.ShowSnackbar("تم إيقاف تشغيل الجامعة"))
            },
            onError = { e ->
                updateState { it.copy(actionLoadingId = null, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Change plan ───────────────────────────────────────────────────────────

    override fun onChangePlanClick(universityId: Int) {
        val item = state.value.universities.find { it.id == universityId } ?: return
        updateState {
            it.copy(
                showPlanDialog  = true,
                planDialogTarget = item,
                changePlanForm  = UniversitiesUiState.ChangePlanForm(
                    plan          = item.plan ?: "trial",
                    planExpiresAt = item.planExpiresAt ?: "",
                    maxStudents   = item.maxStudents.toString(),
                    maxStaff      = item.maxStaff.toString(),
                ),
            )
        }
    }

    override fun onPlanDialogPlanChange(value: String)        = patchPlanForm { it.copy(plan = value) }
    override fun onPlanDialogExpiresAtChange(value: String)   = patchPlanForm { it.copy(planExpiresAt = value) }
    override fun onPlanDialogMaxStudentsChange(value: String) = patchPlanForm { it.copy(maxStudents = value) }
    override fun onPlanDialogMaxStaffChange(value: String)    = patchPlanForm { it.copy(maxStaff = value) }

    private fun patchPlanForm(block: (UniversitiesUiState.ChangePlanForm) -> UniversitiesUiState.ChangePlanForm) {
        updateState { it.copy(changePlanForm = block(it.changePlanForm)) }
    }

    override fun onConfirmChangePlan() {
        val target = state.value.planDialogTarget ?: return
        val form   = state.value.changePlanForm
        updateState { it.copy(actionLoadingId = target.id) }
        tryToExecute(
            action = {
                repository.changeUniversityPlan(
                    id      = target.id,
                    request = ChangePlanRequest(
                        plan          = form.plan,
                        planExpiresAt = form.planExpiresAt.takeIf { it.isNotBlank() },
                        maxStudents   = form.maxStudents.toIntOrNull(),
                        maxStaff      = form.maxStaff.toIntOrNull(),
                    ),
                )
            },
            onSuccess = {
                updateState { s ->
                    s.copy(
                        actionLoadingId  = null,
                        showPlanDialog   = false,
                        planDialogTarget = null,
                        universities     = s.universities.map { u ->
                            if (u.id == target.id)
                                u.copy(
                                    plan          = form.plan,
                                    planExpiresAt = form.planExpiresAt.takeIf { it.isNotBlank() },
                                    maxStudents   = form.maxStudents.toIntOrNull() ?: u.maxStudents,
                                    maxStaff      = form.maxStaff.toIntOrNull()   ?: u.maxStaff,
                                )
                            else u
                        },
                    )
                }
                sendEffect(UniversitiesEffect.ShowSnackbar("تم تغيير الخطة بنجاح"))
            },
            onError = { e ->
                updateState { it.copy(actionLoadingId = null, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onDismissPlanDialog() {
        updateState { it.copy(showPlanDialog = false, planDialogTarget = null) }
    }

    // ── Private helper ────────────────────────────────────────────────────────

    private fun sendEffect(effect: UniversitiesEffect) {
        sendNewNavigationEffect(effect)   // reuses BaseViewModel's channel
    }
}

// ── Mappers ───────────────────────────────────────────────────────────────────

private fun UniversityDto.toItem() = UniversitiesUiState.UniversityItem(
    id              = id,
    name            = name,
    slug            = slug,
    plan            = plan,
    isActive        = isActive,
    studentCount    = studentCount    ?: stats?.totalStudents    ?: 0,
    doctorCount     = doctorCount     ?: stats?.totalDoctors     ?: 0,
    employeeCount   = employeeCount   ?: stats?.totalEmployees   ?: 0,
    departmentCount = departmentCount ?: stats?.totalDepartments ?: 0,
    activeCourses   = courseCount     ?: stats?.activeCourses    ?: 0,
    planExpiresAt   = planExpiresAt,
    maxStudents     = maxStudents,
    maxStaff        = maxStaff,
    createdAt       = createdAt,
    nameEn          = nameEn,
    email           = email,
    phone           = phone,
    address         = address,
    country         = country,
)

private fun UniversityAdminDto.toAdminItem() = UniversitiesUiState.UniversityAdminItem(
    id          = id,
    name        = name,
    email       = email,
    isActive    = isActive,
    lastLoginAt = lastLoginAt,
    createdAt   = createdAt,
)
