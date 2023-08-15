package ru.ragefalcon.mastergym_android.viewmodel

import bdElement.BaseClient
import bdElement.BaseTraining
import bdElement.CommonPageList
import bdElement.ParamCommonSpisRequest
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import ru.ragefalcon.mastergym_android.global.myJson
import ru.ragefalcon.mastergym_android.viewmodel.helpers.SpisPageLoaded
import ru.ragefalcon.mastergym_android.viewmodel.helpers.commonUserPostJsonRequest
import ru.ragefalcon.mastergym_android.viewmodel.helpers.parseMyResponse

class MainDateList(vm: MainViewModel) {
    /**
     * Тренировки которые в данный момент доступны клиенту для выполнения.
     * */
    val openTrainingsForClient = SpisPageLoaded<BaseTraining>(6, vm) { limit, skip, load ->
        vm.userProfile.value?.id?.let { profileId ->
            commonUserPostJsonRequest(
                vm = vm,
                path = "open_trainings",
                bodyStrJson = myJson.encodeToString(ParamCommonSpisRequest(limit = limit, skip = skip)),
                headers = {
                    append("clientId", profileId)
                }
            ).let { userStr ->
                parseMyResponse<List<BaseTraining>>(userStr).run {
                    if (checkStatusOK()) load(CommonPageList(objectResponse, totalCount))
                }
            }
        }
    }

    /**
     * Тренировки которые в данный момент УЖЕ не доступны клиенту для выполнения.
     * */
    val oldTrainingsForClient = SpisPageLoaded<BaseTraining>(12, vm) { limit, skip, load ->
        vm.userProfile.value?.id?.let { profileId ->
            commonUserPostJsonRequest(
                vm = vm,
                path = "close_trainings",
                bodyStrJson = myJson.encodeToString(ParamCommonSpisRequest(limit = limit, skip = skip)),
                headers = {
                    append("clientId", profileId)
                }
            ).let { userStr ->
                parseMyResponse<List<BaseTraining>>(userStr).run {
                    if (checkStatusOK()) load(CommonPageList(objectResponse, totalCount))
                }
            }
        }
    }

    /**
     * Список тренеров у клиента.
     * */
    val listTrainersOpen = SpisPageLoaded<BaseClient>(0,vm) { limit, skip, load ->
        vm.userProfile.value?.id?.let { profileId ->
            commonUserPostJsonRequest(
                vm = vm,
                path = "get_trainers_for_this_clients",
                bodyStrJson = myJson.encodeToString(ParamCommonSpisRequest(limit = limit, skip = skip)),
                headers = {
                    append("clientId", profileId)
                    append("close", "false")
                }
            ).let { userStr ->
                parseMyResponse<List<BaseClient>>(userStr).run {
                    /**
                     * Кусок с фильтрацией ?.filter { it.status == "new" || it.status == "open" }
                     * нужен для правильной работы на старой версии сервера, после обновления сервера это не понадобится.
                     * */
                    if (checkStatusOK()) load(CommonPageList(objectResponse?.filter { it.status == "new" || it.status == "open" }, totalCount))
                }
            }
        }
    }

    /**
     * Список бывших или отказавших тренеров у клиента.
     * */
    val listTrainersClose = SpisPageLoaded<BaseClient>(0,vm) { limit, skip, load ->
        vm.userProfile.value?.id?.let { profileId ->
            commonUserPostJsonRequest(
                vm = vm,
                path = "get_trainers_for_this_clients",
                bodyStrJson = myJson.encodeToString(ParamCommonSpisRequest(limit = limit, skip = skip)),
                headers = {
                    append("clientId", profileId)
                    append("close", "true")
                }
            ).let { userStr ->
                parseMyResponse<List<BaseClient>>(userStr).run {
                    /**
                     * Кусок с фильтрацией ?.filter { it.status != "new" && it.status != "open" }
                     * нужен для правильной работы на старой версии сервера, после обновления сервера это не понадобится.
                     * */
                    if (checkStatusOK()) load(CommonPageList(objectResponse?.filter { it.status != "new" && it.status != "open" }, totalCount))
                }
            }
        }
    }

}