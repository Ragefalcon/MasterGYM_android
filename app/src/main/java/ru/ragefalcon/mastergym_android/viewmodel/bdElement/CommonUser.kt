package bdElement

import extension.getCurrentDateTimeUTC
import ru.ragefalcon.mastergym_android.viewmodel.helpers.TypeElement

//@Serializable
open class CommonUser() : CommonElement() {
    open val id: String? = null
    open val email: String? = null
    open val name: String? = null
    open val second_name: String? = null
    open val phone: String? = null
    open val avatar: String? = null
    open val description: String? = null
    open val status: String? = null
    override val date_create: Long? = getCurrentDateTimeUTC()
    override val date_update: Long? = null
    override val typeElement: TypeElement = TypeElement.Client
}