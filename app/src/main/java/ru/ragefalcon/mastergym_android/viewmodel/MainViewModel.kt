package ru.ragefalcon.mastergym_android.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bdElement.BaseTraining
import bdElement.UserProfile
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private var _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private var _isLoading = MutableStateFlow<Boolean?>(null)
    val isLoading: StateFlow<Boolean?> = _isLoading

    val date = MainDateList(this)
    val func = SpisAllRequests(this, _userProfile)

    var selectedTraining by mutableStateOf<BaseTraining?>(null)

    var archiveTrainings by  mutableStateOf(false)
    var archiveTrainer by  mutableStateOf(false)

    fun setCurrUser(newUser: FirebaseUser?) {
        _currentUser.value = newUser
        Log.d("myTag", "setCurrentUser")
        viewModelScope.launch {
            Log.d("myTag", "startRequest.getUserProfile()_1")
            _userProfile.value = func.startRequest.getUserProfile()
            Log.d("myTag", "startRequest.getUserProfile()_2")
            date.openTrainingsForClient.update()
            date.oldTrainingsForClient.update()
            date.listTrainersOpen.update()
            date.listTrainersClose.update()
            Log.d("myTag", "startRequest.getUserProfile()_3")
        }
    }

}