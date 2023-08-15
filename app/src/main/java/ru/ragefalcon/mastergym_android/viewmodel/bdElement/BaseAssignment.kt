package bdElement

import kotlinx.serialization.Serializable
import ru.ragefalcon.mastergym_android.viewmodel.bdElement.DragBaseElement

@Serializable
data class BaseAssignment(
    override val id: String? = null,
    var name: String? = null,
    var description: String? = null,
    override var order: Int? = null,
    var video_id: String? = null,
    var muscle_group_tags: List<String>? = null,
    var bunny_library: String? = null,
    var bunny_guid: String? = null
): DragBaseElement {
}
