package bdElement

import kotlinx.serialization.Serializable


@Serializable
data class MyTags(
    var id: String? = null,
    val name: String = ""
)
