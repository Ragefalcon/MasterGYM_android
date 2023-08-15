package ru.ragefalcon.mastergym_android.view.pages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import bdElement.UserProfile
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.firebase.ui.auth.AuthUI
import extension.getCurrentDateTimeUTC
import kotlinx.coroutines.launch
import ru.ragefalcon.mastergym_android.R
import ru.ragefalcon.mastergym_android.global.appConst
import ru.ragefalcon.mastergym_android.view.LoaderComponent
import ru.ragefalcon.mastergym_android.view.elements.CommonText
import ru.ragefalcon.mastergym_android.view.elements.HeaderText
import ru.ragefalcon.mastergym_android.view.elements.SubheaderText
import ru.ragefalcon.mastergym_android.view.helpers.PhoneField
import ru.ragefalcon.mastergym_android.view.helpers.PhoneVisualTransformation
import ru.ragefalcon.mastergym_android.viewmodel.MainViewModel

@Composable
fun PageProfile(vm: MainViewModel) {

    val user: UserProfile? by vm.userProfile.collectAsState()

    val imageUri = remember {
        mutableStateOf<Uri?>(null)
    }
    val uploadImgProc = remember { mutableStateOf(false) }
    val uploadImgProgress = remember { mutableStateOf(0.0) }

    val editProfile = remember { mutableStateOf(false) }
    val editName = remember { mutableStateOf(user?.name ?: "") }
    val editPhone = remember { mutableStateOf(user?.phone ?: "") }
    val enableChange = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BlockAvatar(
            user,
            imageUri = imageUri,
            uploadImgProc = uploadImgProc,
            uploadImgProgress = uploadImgProgress,
            editProfile = editProfile
        )
        BlockEditTextProperties(
            user,
            editProfile = editProfile,
            editName = editName,
            editPhone = editPhone
        )
        if (editProfile.value) {
            BlockButtInEdit(
                vm, user,
                imageUri = imageUri,
                uploadImgProc = uploadImgProc,
                uploadImgProgress = uploadImgProgress,
                editProfile = editProfile,
                editName = editName,
                editPhone = editPhone,
                enableChange = enableChange
            )
        } else {
            BlockBottomButt(vm) {
                editProfile.value = true
            }
        }
    }
}

@Composable
private fun BlockAvatar(
    user: UserProfile?,
    imageUri: MutableState<Uri?>,
    uploadImgProc: MutableState<Boolean>,
    uploadImgProgress: MutableState<Double>,
    editProfile: MutableState<Boolean>
) {
//    val contentResolver: ContentResolver = LocalContext.current.contentResolver
    val ripple = rememberRipple(bounded = true, color = LocalContentColor.current.copy(alpha = ContentAlpha.medium))

    val bitmap = remember {
        mutableStateOf<Bitmap?>(null)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
        Log.d("myTag", "uri = $uri")
    }
    if (imageUri.value != null && editProfile.value) {
        imageUri.value?.let {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap.value = MediaStore.Images.Media.getBitmap(LocalContext.current.contentResolver, it)

            } else {
                val source = ImageDecoder
                    .createSource(LocalContext.current.contentResolver, it)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }


            bitmap.value?.let { btm ->
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(top = 60.dp, bottom = 10.dp)
                        .width(210.dp)
                        .height(210.dp)
                        .clip(CircleShape)
                )
            }
        }
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user?.avatar ?: stringResource(R.string.temp_avatar_url))
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.app_logo),
            contentDescription = "avatar user",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(top = 60.dp, bottom = 10.dp)
                .width(210.dp)
                .height(210.dp)
                .clip(CircleShape)
        )
    }
    if (editProfile.value) {
        Box(
            modifier = Modifier.padding(appConst.paddingCommon)
                .border(1.dp, colorResource(R.color.myOrange), shape = RoundedCornerShape(15.dp))
                .clip(RoundedCornerShape(15.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple
                ) {
                    launcher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            SubheaderText(
                stringResource(R.string.label_butt_change_avatar),
                color = colorResource(R.color.myOrange),
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp)
            )
            if (uploadImgProc.value) Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .fillMaxWidth(uploadImgProgress.value.toFloat())
                        .fillMaxHeight(uploadImgProgress.value.toFloat())
                        .background(colorResource(R.color.myOrange), RoundedCornerShape(15.dp))
                        .alpha(0.5f)
                )
            }
        }
    }
}

@Composable
private fun BlockEditTextProperties(
    user: UserProfile?,
    editProfile: MutableState<Boolean>,
    editName: MutableState<String>,
    editPhone: MutableState<String>
) {
    val transform = PhoneVisualTransformation(mask = "+7 (000) 000 00 00", maskNumber = '0')
    if (editProfile.value) {
        TextField(
            value = editName.value,
            onValueChange = {
                editName.value = it
            },
            label = {
                Text(stringResource(R.string.your_name))
            },
            modifier = Modifier.padding(horizontal = 40.dp).fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = appConst.fontHeaderSize,
                color = colorResource(R.color.my_color_font_header)
            ),
            singleLine = true
        )
    } else HeaderText(user?.name ?: "", modifier = Modifier.padding(appConst.paddingSmall))
    CommonText(
        user?.email ?: "",
        color = colorResource(R.color.fontCommonColor),
        modifier = Modifier.padding(top = 40.dp),
    )
    if (editProfile.value) {
        PhoneField(editPhone.value,
            mask = "+7 (000) 000 00 00",
            modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp).fillMaxWidth(),
            maskNumber = '0',
            onPhoneChanged = { editPhone.value = it }
        )
    } else CommonText(
        transform.filter(AnnotatedString(user?.phone ?: "")).text.text,
        color = colorResource(R.color.fontCommonColor),
        modifier = Modifier.padding(appConst.paddingSmall).padding(bottom = 50.dp)
    )

}

@Composable
private fun BlockButtInEdit(
    vm: MainViewModel,
    user: UserProfile?,
    imageUri: MutableState<Uri?>,
    uploadImgProc: MutableState<Boolean>,
    uploadImgProgress: MutableState<Double>,
    editProfile: MutableState<Boolean>,
    editName: MutableState<String>,
    editPhone: MutableState<String>,
    enableChange: MutableState<Boolean>
) {
    val context = LocalContext.current.applicationContext
    val ripple = rememberRipple(bounded = true, color = LocalContentColor.current.copy(alpha = ContentAlpha.medium))
    Row(Modifier.fillMaxWidth().padding(top = 50.dp)) {
        Box(
            modifier = Modifier.padding(appConst.paddingCommon)
                .weight(1f)
                .border(1.dp, colorResource(R.color.myOrange), shape = RoundedCornerShape(15.dp))
                .clip(RoundedCornerShape(15.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple
                ) {
                    editProfile.value = false
                },
            contentAlignment = Alignment.Center
        ) {
            SubheaderText(
                stringResource(R.string.label_butt_cancel),
                color = colorResource(R.color.myOrange),
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp)
            )
        }
        Box(
            modifier = Modifier.padding(appConst.paddingCommon)
                .weight(1f)
                .background(colorResource(R.color.myOrange), shape = RoundedCornerShape(15.dp))

                .alpha(if (enableChange.value) 1f else 0.5f)
                .clip(RoundedCornerShape(15.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple
                ) {
                    UpdateAvatarFunk(
                        vm, user,
                        imageUri = imageUri,
                        uploadImgProc = uploadImgProc,
                        uploadImgProgress = uploadImgProgress,
                        editProfile = editProfile,
                        editName = editName,
                        editPhone = editPhone,
                        enableChange = enableChange,
                        context = context
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            SubheaderText(
                stringResource(R.string.label_butt_change),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp)
                    .alpha(if (enableChange.value.not()) 0.5f else 1f)
            )
            if (enableChange.value.not()) LoaderComponent(Modifier.matchParentSize())
        }
    }
}

private fun UpdateAvatarFunk(
    vm: MainViewModel,
    user: UserProfile?,
    imageUri: MutableState<Uri?>,
    uploadImgProc: MutableState<Boolean>,
    uploadImgProgress: MutableState<Double>,
    editProfile: MutableState<Boolean>,
    editName: MutableState<String>,
    editPhone: MutableState<String>,
    enableChange: MutableState<Boolean>,
    context: Context
) {
    if (enableChange.value) {
        vm.viewModelScope.launch {
            enableChange.value = false
            if (user?.name != editName.value)
                vm.func.profile.setNewNameUser(editName.value)
            if (user?.phone != editPhone.value && editPhone.value.length == 10)
                vm.func.profile.setNewPhoneNumberUser(editPhone.value)
            imageUri.value?.let {
                vm.func.profile.uploadImage(context,
                    uri = it,
                    fileName = "avatar_${user?.id}_${getCurrentDateTimeUTC()}",
                    maxWidth = 900,
                    maxHeight = 900,
                    pathBackend = "upload_image",
                    uploadImgStart = uploadImgProc,
                    uploadImgProgress = {
                        uploadImgProgress.value = it
                    }
                ).let { urlAvatar ->
                    if (urlAvatar != "") {
                        vm.func.profile.setNewAvatarUser(urlAvatar)
                        imageUri.value = null
                        uploadImgProgress.value = 0.0
                    }
                }
            }
            enableChange.value = true
            editProfile.value = false
        }
    }
}

@Composable
private fun BlockBottomButt(vm: MainViewModel, toEdit: () -> Unit) {
    val context = LocalContext.current.applicationContext
    val ripple = rememberRipple(bounded = true, color = LocalContentColor.current.copy(alpha = ContentAlpha.medium))
    Box(
        modifier = Modifier.padding(appConst.paddingCommon)
            .border(1.dp, colorResource(R.color.myOrange), shape = RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple
            ) {
                toEdit()
            },
        contentAlignment = Alignment.Center
    ) {
        SubheaderText(
            stringResource(R.string.label_butt_edit),
            color = colorResource(R.color.myOrange),
            modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp)
        )
    }
    Box(
        modifier = Modifier.padding(appConst.paddingCommon)
            .background(colorResource(R.color.myOrange), shape = RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple
            ) {
                AuthUI
                    .getInstance()
                    .signOut(context)
                    .addOnCompleteListener {
                        vm.setCurrUser(null)
                    }
            },
        contentAlignment = Alignment.Center
    ) {
        SubheaderText(
            stringResource(R.string.label_butt_exit),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 40.dp, vertical = 10.dp)
        )
    }
}