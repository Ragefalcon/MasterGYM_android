package bdElement

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    override val id: String? = null,
    override val email: String? = null,
    override val name: String? = null,
    override val second_name: String? = null,
    override val phone: String? = null,
    override val avatar: String? = null,
    val role: List<String>? = null,
    override val description: String? = null,
    val quiz: Boolean? = null,
    override val status: String? = null,
    val date_registration: Long? = null,
    val tariff: String? = null,
    val tariff_expiration: Long? = null,
) : CommonUser() {
}

