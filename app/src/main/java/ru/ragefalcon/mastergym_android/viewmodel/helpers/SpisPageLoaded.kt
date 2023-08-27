package ru.ragefalcon.mastergym_android.viewmodel.helpers

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import bdElement.CommonPageList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.ragefalcon.mastergym_android.viewmodel.MainViewModel

class SpisPageLoaded<T>(
    sizePage: Int,
    @StringRes val emptyMessage:  Int,
    private val vm: MainViewModel,
    private val request: suspend (Int,Int,(CommonPageList<T>) -> Unit) -> Unit
) {
    private var _spisLoaded = MutableStateFlow<List<T>>(listOf())
    val spisLoaded: StateFlow<List<T>> = _spisLoaded

    private var _loadingProcess = MutableStateFlow<Boolean>(true)
    val loadingProcess: StateFlow<Boolean> = _loadingProcess

    private suspend fun requestList(limit: Int, skip: Int, silent: Boolean, listener: (CommonPageList<T>) -> Unit = {}) {
        if (silent.not()) _loadingProcess.value = true
        request(limit,skip) { load ->
            _spisLoaded.value = load.rows ?: listOf()
            load.totalCount?.let { totalCount = it }
            listener(load)
            _loadingProcess.value = false
        }
    }

    suspend fun update(silent: Boolean = false) = requestList(limit,skip, silent)

    fun setCurrPage(value: Int) {
        if (value < totalPage) {
            skip = pageSize * value
            currentPage = value
        }
        vm.viewModelScope.launch {
            update()
        }
    }

    var totalCount = 0
        get
        private set(value) {
            field = value
            totalPage = if (pageSize != 0 && value != 0) {
                value / pageSize + if (value % pageSize > 0) 1 else 0
            } else 1
            if (totalPage <= currentPage) setCurrPage(0)
        }
    var totalPage by mutableStateOf(0)
        get
        private set
    private var pageSize = sizePage
        get
        set(value) {
            if (value != 0) {
                if(value != field) {
                    field = value
                    totalPage = if (totalCount != 0) {
                        totalCount / value + if (totalCount % value > 0) 1 else 0
                    } else 1
                    if (totalPage <= currentPage) setCurrPage(0)
                    limit = value
                }
            }   else    {
                field = value
                limit = 0
                if (totalPage <= currentPage) setCurrPage(0)
                totalPage = 1
            }
        }

    var limit by mutableStateOf(pageSize)
        private set

    var skip by mutableStateOf(0)
        private set

    var currentPage by mutableStateOf(0)
        private set

}