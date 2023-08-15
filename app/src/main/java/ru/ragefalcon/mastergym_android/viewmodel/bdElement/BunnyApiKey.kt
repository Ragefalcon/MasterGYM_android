package bdElement

import kotlinx.serialization.Serializable

@Serializable
data class BunnyApiKey(
    var signature: String? = null,
    val expire: Long? = null,
    val guid: String? = null,
    val library: Long? = null
)