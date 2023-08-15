package bdElement

import ru.ragefalcon.mastergym_android.viewmodel.helpers.TypeElement

abstract class CommonElement (){
    abstract val date_create: Long?
    abstract val date_update: Long?
    abstract val typeElement: TypeElement
}
