package bdElement

import extension.getCurrentDateTimeUTC
import kotlinx.serialization.Serializable
import ru.ragefalcon.mastergym_android.viewmodel.helpers.TypeElement

@Serializable
data class BaseComplex(
    var id: String? = null,
    var trainer_id: String? = null,
    var cover: String? = null,
    var name: String? = null,
    var description: String? = null,
    var price: Double? = null,
    var deadline: Int? = null,
    var activate: Boolean? = false,
    var archive: Boolean? = false,
    override var date_create: Long? = getCurrentDateTimeUTC(),
    override var date_update: Long? = null,
): CommonElement()  {
    override val typeElement = TypeElement.Complex
}

