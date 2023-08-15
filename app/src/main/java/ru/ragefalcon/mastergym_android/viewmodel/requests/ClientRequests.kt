package ru.ragefalcon.mastergym_android.viewmodel.requests

import android.util.Log
import bdElement.BaseTraining
import ru.ragefalcon.mastergym_android.viewmodel.MainViewModel
import ru.ragefalcon.mastergym_android.viewmodel.helpers.commonUserPostFDParamRequest
import ru.ragefalcon.mastergym_android.viewmodel.helpers.parseMyResponse

class ClientRequests(val vm: MainViewModel) {
    suspend fun setCompletedTrainingFirestore(
        training: BaseTraining,
        completed: Long
    ): Boolean {
        commonUserPostFDParamRequest(
            path = "set_completed_training",
            vm = vm,
            formData = {
                training.id?.let { append("trainingId", it) }
                append("completed", completed.toString())
            }
        ).let { userStr ->
            Log.d("myTag","userStr = $userStr")
            parseMyResponse<String>(userStr).run {
                return status == "OK"
            }
        }
    }
}