package ru.ragefalcon.mastergym_android.viewmodel.requests

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import bdElement.UserProfile
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import ru.ragefalcon.mastergym_android.viewmodel.MainViewModel
import ru.ragefalcon.mastergym_android.viewmodel.helpers.commonUserPostFDParamRequest
import ru.ragefalcon.mastergym_android.viewmodel.helpers.commonUserPostMultiPartRequest
import ru.ragefalcon.mastergym_android.viewmodel.helpers.parseMyResponse
import java.io.ByteArrayOutputStream

class ProfileRequests(val vm: MainViewModel, private var _userProfile: MutableStateFlow<UserProfile?>) {
    suspend fun refreshUserData() {
        _userProfile.value = vm.func.startRequest.getUserProfile()
    }

    suspend fun setNewPhoneNumberUser(
        newPhone: String,
    ): Boolean {
        commonUserPostFDParamRequest(
            path = "set_user_field",
            vm = vm,
            formData = {
                append("fieldName", "phone")
                append("newValue", newPhone)
            }
        ).let { response ->
            parseMyResponse<String>(response).run {
                vm.func.profile.refreshUserData()
                return status == "OK"
            }
        }
    }

    suspend fun setNewNameUser(
        newName: String
    ): Boolean {
        commonUserPostFDParamRequest(
            path = "set_user_field",
            vm = vm,
            formData = {
                append("fieldName", "name")
                append("newValue", newName)
            }
        ).let { response ->
            parseMyResponse<String>(response).run {
                vm.func.profile.refreshUserData()
                return status == "OK"
            }
        }
    }

    suspend fun setNewAvatarUser(
        newAvatarUrl: String
    ): Boolean {
        commonUserPostFDParamRequest(
            path = "set_new_avatar",
            vm = vm,
            formData = {
                append("fileLink", newAvatarUrl)
            }
        ).let { response ->
            parseMyResponse<String>(response).run {
                vm.func.profile.refreshUserData()
                return status == "OK"
            }
        }
    }

    suspend fun uploadImage(
        context: Context,
        uri: Uri,
        fileName: String,
        maxWidth: Int,
        maxHeight: Int,
        pathBackend: String,
        uploadImgStart: MutableState<Boolean>,
        uploadImgProgress: (Double) -> Unit,
    ): String {
        val contentResolver: ContentResolver = context.contentResolver

        val rotatedBitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val source = ImageDecoder
                .createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }

        val width = rotatedBitmap.width
        val height = rotatedBitmap.height
        val shouldResize = (width > maxWidth) || (height > maxHeight)

        val newWidth: Int
        val newHeight: Int

        if (!shouldResize) {
            newWidth = width
            newHeight = height
        } else {
            if (width > height) {
                newHeight = (height * (maxWidth.toDouble() / width.toDouble())).toInt()
                newWidth = maxWidth
            } else {
                newWidth = (width * (maxHeight.toDouble() / height.toDouble())).toInt()
                newHeight = maxHeight
            }
        }

        val resizedBitmap = Bitmap.createScaledBitmap(rotatedBitmap, newWidth, newHeight, false)

        val blob = bitmapToBlob(resizedBitmap)

        val extension = "jpg"
        val fileNameF = "$fileName.$extension"
        uploadImgStart.value = true

        commonUserPostMultiPartRequest(
            path = pathBackend,
            vm = vm,
            formData = {
                append("fileName", fileNameF)
                append("file", blob, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileNameF\"")
                })
            },
            onUpload = { send, total ->
                (send.toDouble() / total.toDouble()).let { progr ->
                    (if (progr > 1.0) 1.0 else progr).let(uploadImgProgress)
                }
            }
        ).let { _ ->
            uploadImgStart.value = false
            return "https://fitconstructorimg.b-cdn.net/avatar/$fileNameF"
        }
    }

    private fun bitmapToBlob(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

}