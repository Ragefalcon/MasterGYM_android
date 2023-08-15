package ru.ragefalcon.mastergym_android.viewmodel.helpers

import bdElement.CommonPageList
import kotlinx.serialization.Serializable
import ru.ragefalcon.mastergym_android.global.myJson

@Serializable
data class CustomMyResponse<T>(
    val status: String? = null,
    val objectResponse: T? = null,
    val totalCount: Int? = null,
    val skip: Int? = null,
    val message: String? = null
) {

    fun checkStatusOK() = status == "OK"
}

@Serializable
data class CustomMyListResponse<R>(
    val status: String? = null,
    val objectResponse: List<R>? = null,
    val totalCount: Int? = null,
    val skip: Int? = null,
    val message: String? = null
) {
    fun checkStatusOK(rezFun: (CustomMyListResponse<R>) -> Unit) {
        if (status == "OK") rezFun(this)
    }

    fun getCommonPageList(): CommonPageList<R> = CommonPageList(objectResponse, totalCount)

}

inline fun <reified T> parseMyResponse(json: String): CustomMyResponse<T> {
    return myJson.decodeFromString<CustomMyResponse<T>>(json)
}

inline fun <reified R> parseMyListResponse(json: String): CustomMyListResponse<R> {

    return myJson.decodeFromString<CustomMyListResponse<R>>(json)
}

