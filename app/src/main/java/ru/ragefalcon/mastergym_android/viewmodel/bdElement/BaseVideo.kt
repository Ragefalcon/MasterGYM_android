package bdElement

import kotlinx.serialization.Serializable

@Serializable
data class BaseVideo(
    var id: String? = null,
    val common: Boolean? = null,
    val author_id: String? = null,
    val date_upload: Long? = null,
    var muscle_group_tags_id: List<String>? = null,
    var equipment_type_tags_id: List<String>? = null,
    var sex_id: List<String>? = null,
    val bunny_library: String = "",
    val name: String = "",
    val bunny_guid: String = ""
) {
}

