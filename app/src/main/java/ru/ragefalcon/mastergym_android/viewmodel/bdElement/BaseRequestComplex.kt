package bdElement

import extension.getCurrentDateTimeUTC
import kotlinx.serialization.Serializable
import ru.ragefalcon.mastergym_android.viewmodel.helpers.TypeElement

@Serializable
data class BaseRequestComplex(
    var id: String? = null,
    var trainer_id: String? = null,
    var complex_id: String? = null,
    var complex_cover: String? = null,
    var complex_name: String? = null,
    var complex_price: Double? = null,
    var client_id: String? = null,
    var client_name: String? = null,
    var client_avatar: String? = null,
    var client_phone: String? = null,
    var client_message: String? = null,
    var status: String? = "new",
    override var date_create: Long? = getCurrentDateTimeUTC(),
    override var date_update: Long? = null,
) : CommonElement() {
    override val typeElement = TypeElement.RequestComplex
}
