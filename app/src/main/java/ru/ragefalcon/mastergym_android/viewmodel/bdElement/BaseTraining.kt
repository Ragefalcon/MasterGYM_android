package bdElement

import extension.getCurrentDateTimeUTC
import kotlinx.serialization.Serializable
import ru.ragefalcon.mastergym_android.viewmodel.bdElement.DragBaseElement
import ru.ragefalcon.mastergym_android.viewmodel.helpers.TypeElement

@Serializable
data class BaseTraining(
    override val id: String? = null,
    override var order: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val description_title: String? = null,
    val delete_flag: String? = null,
    val client_id: String? = null,
    override val date_create: Long? = getCurrentDateTimeUTC(),
    override val date_update: Long? = null,
    var date_open: Long? = null,
    val count_days_open: Int? = null,
    val date_close: Long? = null,
    val trainer_id: String? = null,
    val muscle_group_tags: List<String>? = null,
    var completed: Long? = null,
    var assignments: List<BaseAssignment> = listOf(),
    val status: String = "",
    val activate: Boolean = false,
    val complex_id: String? = null,
    val complex_date_start: Long? = null,
    val complex_cover: String? = null
) : CommonElement(), DragBaseElement {
    override val typeElement = TypeElement.Training
}


