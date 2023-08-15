package ru.ragefalcon.mastergym_android.viewmodel

import bdElement.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import ru.ragefalcon.mastergym_android.viewmodel.requests.ClientRequests
import ru.ragefalcon.mastergym_android.viewmodel.requests.ProfileRequests
import ru.ragefalcon.mastergym_android.viewmodel.requests.StartRequest

class SpisAllRequests(vm: MainViewModel,  private var _userProfile: MutableStateFlow<UserProfile?>) {
    val startRequest = StartRequest(vm)
    val client = ClientRequests(vm)
    val profile = ProfileRequests(vm, _userProfile)
}