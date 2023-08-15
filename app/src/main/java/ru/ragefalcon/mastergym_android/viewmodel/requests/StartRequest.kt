package ru.ragefalcon.mastergym_android.viewmodel.requests

import bdElement.UserProfile
import ru.ragefalcon.mastergym_android.viewmodel.MainViewModel
import ru.ragefalcon.mastergym_android.viewmodel.helpers.commonUserGetRequest
import ru.ragefalcon.mastergym_android.viewmodel.helpers.parseMyResponse

class StartRequest(private val vm: MainViewModel) {
    suspend fun getUserProfile(
    ): UserProfile? {
        commonUserGetRequest("get_user_profile", vm, {
        }).let { userStr ->
            if (userStr != "") parseMyResponse<UserProfile>(userStr).run {
                return if (checkStatusOK()) objectResponse else null
            }   else return null
        }
    }

}
