package bdElement

import extension.getCurrentDateTimeUTC
import kotlinx.serialization.Serializable

@Serializable
data class BaseClient(
    override var id: String? = null,
    override var email: String? = null,
    override var name: String? = null,
    override var second_name: String? = null,
    override var phone: String? = null,
    override var avatar: String? = null,
    override var description: String? = null,
    override var status: String? = null,
    override var date_create: Long? = getCurrentDateTimeUTC(),
    override var date_update: Long? = null,
    val date_open: Long? = null,
    val date_close: Long? = null,
    var zayavka_phone: String? = null,
    var zayavka_id: String? = null,
) : CommonUser() {
}
