package ru.ragefalcon.mastergym_android.viewmodel.helpers

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.tasks.await
import ru.ragefalcon.mastergym_android.global.serverAdress
import ru.ragefalcon.mastergym_android.viewmodel.MainViewModel

suspend fun commonUserGetRequest(
    path: String,
    vm: MainViewModel,
    headers: HeadersBuilder.() -> Unit = {}
): String {
    vm.currentUser.value?.getIdToken(false)?.await()?.token?.let { token ->
        val client = HttpClient()
        return client.get("$serverAdress/$path") {
            method = HttpMethod.Get
            headers {

                append("accessToken", token)
                append("Accept", "*/*")
                headers.invoke(this)
            }
        }.bodyAsText()
    }
    return ""
}

suspend fun commonUserPostJsonRequest(
    path: String,
    bodyStrJson: String = "",
    vm: MainViewModel,
    headers: HeadersBuilder.() -> Unit = {},
): String {
    vm.currentUser.value?.getIdToken(false)?.await()?.token?.let { token ->
        val client = HttpClient()
        return client.post("$serverAdress/$path") {
            method = HttpMethod.Post
            headers {

                append("accessToken", token)
                append("Content-Type", "application/json")
                append("Accept", "*/*")
                headers.invoke(this)
            }
            setBody(bodyStrJson)
        }.bodyAsText()
    }
    return ""
}

suspend fun commonUserPostFDParamRequest(
    path: String,
    vm: MainViewModel,
    headers: HeadersBuilder.() -> Unit = {},
    formData: ParametersBuilder.() -> Unit = {}
): String {
    vm.currentUser.value?.getIdToken(false)?.await()?.token?.let { token ->
        vm.userProfile.value?.id?.let { profileID ->

            val client = HttpClient()
            return client.post("$serverAdress/$path") {
                method = HttpMethod.Post
                headers {

                    append("accessToken", token)
                    append("Accept", "*/*")
                    headers.invoke(this)
                }
                setBody(FormDataContent(Parameters.build {
                    append("idProfile", profileID)
                    formData.invoke(this)
                }))
            }.bodyAsText()
        }
    }
    return ""
}

suspend fun commonUserPostMultiPartRequest(
    path: String,
    vm: MainViewModel,
    headers: HeadersBuilder.() -> Unit = {},
    formData: FormBuilder.() -> Unit = {},
    onUpload: (Long, Long) -> Unit = { _, _ -> }
): String {
    vm.currentUser.value?.getIdToken(false)?.await()?.token?.let { token ->
        val client = HttpClient()
        return client.post("$serverAdress/$path") {
            method = HttpMethod.Post
            headers {
                append("accessToken", token)
                append("Accept", "*/*")
                headers.invoke(this)
            }
            setBody(
                MultiPartFormDataContent(
                    formData {
                        formData.invoke(this)
                    },
                    boundary = "WebAppBoundary"
                )
            )
            onUpload { bytesSentTotal, contentLength ->
                onUpload(bytesSentTotal, contentLength)
            }
        }.bodyAsText()
    }
    return ""
}
