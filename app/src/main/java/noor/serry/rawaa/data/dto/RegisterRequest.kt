package noor.serry.rawaa.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Sealed hierarchy that maps to POST /api/auth/register.
 *
 * Use [StudentRegisterRequest] when role == "student"
 * Use [DoctorRegisterRequest]  when role == "doctor"
 *
 * Both share the common fields (university_slug, name, email, password,
 * role, phone) and add their own role-specific fields.
 */
    @Serializable
    data class StudentRegisterRequest(
        @SerialName("university_slug") val universitySlug: String,
        @SerialName("name")            val name: String,
        @SerialName("email")           val email: String,
        @SerialName("password")        val password: String,
        @SerialName("role")            val role: String = "student",
        @SerialName("phone")           val phone: String,
        @SerialName("national_id")     val nationalId: String,
        @SerialName("department_id")   val departmentId: Int,
        @SerialName("level")           val level: Int,
        @SerialName("enrollment_year") val enrollmentYear: Int,
    )
    @Serializable
    data class DoctorRegisterRequest(
        @SerialName("university_slug") val universitySlug: String,
        @SerialName("name")            val name: String,
        @SerialName("email")           val email: String,
        @SerialName("password")        val password: String,
        @SerialName("role")            val role: String,
        @SerialName("phone")           val phone: String,
        @SerialName("role_title")      val roleTitle: String,
        @SerialName("salary")          val salary: Double,
        @SerialName("department_id")   val departmentId: Int,
    )
