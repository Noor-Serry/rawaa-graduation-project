package noor.serry.rawaa.ui.screens.users_admin

/**
 * UI state for the Users Admin screen.
 *
 * Data sourced from:
 *   • GET /api/students   → StudentDto  (name, email, phone, departmentName, level, gpa, isActive, createdAt)
 *   • GET /api/employees  → EmployeeDto (name, email, phone, role, roleTitle, departmentName, isActive)
 *   • GET /api/departments → DepartmentDto (id, name)
 *
 * The unified [UserItem] list matches the design: every card shows role badge,
 * email, phone (if available), department, and join date (if available).
 *
 * Actions available from backend:
 *   • DELETE /api/students/{id}   → deleteStudent    (حذف on a student)
 *   • DELETE /api/employees/{id}  → deleteEmployee   (حذف on a doctor/employee)
 *   • GET    /api/students/{id}   → navigate to detail (عرض الملف on a student)
 *   • GET    /api/employees/{id}  → navigate to detail (عرض الملف on a doctor/employee)
 *   • POST   /api/students        → createStudent    (إضافة مستخدم جديد)
 *   • POST   /api/employees       → createEmployee   (إضافة مستخدم جديد)
 *
 * NOTE: "تعديل" (edit) is intentionally omitted from this list screen because
 * update is surfaced from the detail screen, not the list.
 */
data class UsersAdminUiState(
    val users: List<UserItem> = emptyList(),
    val departments: List<DepartmentFilterItem> = emptyList(),
    val selectedRole: RoleFilter = RoleFilter.ALL,
    val selectedDepartmentId: Int? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** Set when the delete-confirmation dialog should be shown */
    val pendingDeleteId: Int? = null,
    val pendingDeleteType: UserType? = null,
    /** Whether the Add-User bottom sheet is open */
    val showAddUserSheet: Boolean = false,
) {

    // ── Computed stats (matches the 4-chip header row in the design) ──────────

    val totalCount: Int get() = users.size
    val studentCount: Int get() = users.count { it.userType == UserType.STUDENT }
    val doctorCount: Int get() = users.count { it.userType == UserType.DOCTOR }
    val adminCount: Int get() = users.count { it.userType == UserType.ADMIN }

    // ── Filtered list (applied in the ViewModel, cached here for the UI) ──────

    val filteredUsers: List<UserItem>
        get() {
            var list = users
            if (selectedRole != RoleFilter.ALL) {
                list = list.filter { it.userType == selectedRole.toUserType() }
            }
            if (selectedDepartmentId != null) {
                list = list.filter { it.departmentId == selectedDepartmentId }
            }
            val q = searchQuery.trim()
            if (q.isNotEmpty()) {
                list = list.filter {
                    it.name.contains(q, ignoreCase = true) ||
                            it.email.contains(q, ignoreCase = true)
                }
            }
            return list
        }

    // ── Domain models ─────────────────────────────────────────────────────────

    /**
     * Unified view of a student OR an employee, exposing only the fields
     * the backend actually returns in the list endpoints.
     */
    data class UserItem(
        val id: Int,
        val userType: UserType,
        /** raw role string: "student" | "doctor" | "employee" | "admin" */
        val role: String,
        val name: String,
        val email: String,
        /** phone: StudentDto.phone / EmployeeDto.phone  (nullable – not always set) */
        val phone: String?,
        /** StudentDto.departmentName / EmployeeDto.departmentName */
        val departmentId: Int?,
        val departmentName: String?,
        /** StudentDto.isActive == 1 / EmployeeDto.isActive == 1 */
        val isActive: Boolean,
        /** StudentDto.createdAt / null for employees (EmployeeDto has no createdAt in this version) */
        val createdAt: String?,
        /** StudentDto.level – null for employees */
        val level: Int?,
        /** EmployeeDto.roleTitle – null for students */
        val roleTitle: String?,
    )

    data class DepartmentFilterItem(val id: Int, val name: String)

    enum class UserType { STUDENT, DOCTOR, ADMIN }

    enum class RoleFilter(val labelAr: String) {
        ALL("الكل"),
        STUDENT("طالب"),
        DOCTOR("مدرس"),
        ADMIN("إدارة"),
        ;

        fun toUserType(): UserType? = when (this) {
            ALL -> null
            STUDENT -> UserType.STUDENT
            DOCTOR -> UserType.DOCTOR
            ADMIN -> UserType.ADMIN
        }
    }
}
