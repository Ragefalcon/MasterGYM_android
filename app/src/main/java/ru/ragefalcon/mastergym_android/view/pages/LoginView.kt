package ru.ragefalcon.mastergym_android.view.pages

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import ru.ragefalcon.mastergym_android.R
import ru.ragefalcon.mastergym_android.view.FullScreenLoaderComponent
import ru.ragefalcon.mastergym_android.view.elements.SignInButton
import ru.ragefalcon.mastergym_android.viewmodel.MainViewModel


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginView(
    signInLauncher: ActivityResultLauncher<Intent>,
    isError: Boolean = false,
    vm: MainViewModel
) {
    val isLoading by vm.isLoading.collectAsState()
    val context = LocalContext.current.applicationContext
    Scaffold {
        if (isLoading == true && !isError) {
            FullScreenLoaderComponent()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.weight(1F))
                Image(
                    painterResource(id = R.drawable.app_logo),
                    contentDescription = stringResource(R.string.app_logo_desc),
                    modifier = Modifier.width(200.dp).height(200.dp)
                )
                Spacer(modifier = Modifier.weight(1F))
                SignInButton(onClick = {
                    val providers = arrayListOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                    )

                    val signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build()

                    signInLauncher.launch(signInIntent)
                })
                Spacer(modifier = Modifier.weight(1F))
                Text(
                    text = stringResource(R.string.app_login_bottom),
                    textAlign = TextAlign.Center,
                    color = colorResource(R.color.myBlue),
                    modifier = Modifier.clickable {

                        val url = "https://www.mastergym.online"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            2432,//NOTIFICATION_REQUEST_CODE,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )

                        pendingIntent.send()

                    }
                )

                when {
                    isError -> {
                        isError.let {
                            Text(
                                stringResource(R.string.auth_error_msg),
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.error
                            )
                        }
                    }
                }
            }
        }
    }
}
