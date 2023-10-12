package ru.ragefalcon.mastergym_android

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.BottomNavigation
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.ragefalcon.mastergym_android.view.elements.MainItemsMenu
import ru.ragefalcon.mastergym_android.view.pages.LoginView
import ru.ragefalcon.mastergym_android.view.pages.PageProfile
import ru.ragefalcon.mastergym_android.view.pages.PageSelectedTraining
import ru.ragefalcon.mastergym_android.view.pages.PageTrainings
import ru.ragefalcon.mastergym_android.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    val vm by viewModels<MainViewModel>()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            vm.setCurrUser(user)
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = Firebase.auth.currentUser
        currentUser?.let {
            vm.setCurrUser(it)
        }


        var load by mutableStateOf(false)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                delay(2000)
                load = true
            }
        }

        setContent {
            var backPressCount by remember { mutableStateOf(0) }

            BackHandler(true) {
                if (backPressCount == 1){
                    finishAffinity()
                }   else    {
                    backPressCount = 1
                    Toast.makeText(this, getString(R.string.toast_message_backpress_exit), LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        delay(2000)
                        backPressCount = 0
                    }
                }
            }

            val alphaMain: Float by animateFloatAsState(
                targetValue = if (load) 1f else 0f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )

            if (load) Column(Modifier.fillMaxSize().background(Color.White).alpha(alphaMain)) {
                MainContent(vm, signInLauncher)
            }
        }
    }
}



@Composable
private fun ColumnScope.MainContent(vm: MainViewModel, signInLauncher: ActivityResultLauncher<Intent>){
    val currUser by vm.currentUser.collectAsState()
    var selectedItem by remember {
        mutableStateOf(MainItemsMenu.Trainings)
    }
    if (currUser == null) LoginView(signInLauncher, false, vm)
    else {
        vm.selectedTraining?.let {
            PageSelectedTraining(vm, it)
        } ?: run {
            Column(modifier = Modifier.weight(1f).background(MaterialTheme.colors.background)) {
                when (selectedItem) {
                    MainItemsMenu.Trainings -> PageTrainings(vm)
                    MainItemsMenu.Profile -> PageProfile(vm)
                }
            }
            BottomNavigation(
                modifier = Modifier,
                elevation = 10.dp,
                backgroundColor = Color.White,
                contentColor = colorResource(R.color.myOrange)
            ) {
                MainItemsMenu.values().forEach { menuItem ->
                    with(menuItem.item) {
                        MyButtonNavigation(
                            selected = (selectedItem == menuItem),
                            onClick = {
                                selectedItem = menuItem
                            },
                            imgActiv = imgActiv,
                            img = img
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.MyButtonNavigation(
    selected: Boolean,
    @DrawableRes imgActiv: Int,
    @DrawableRes img: Int,
    onClick: () -> Unit
) {
    val ripple = rememberRipple(bounded = false, color = LocalContentColor.current.copy(alpha = ContentAlpha.medium))
    Box(
        Modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = true,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple
            ).align(Alignment.CenterVertically)
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.height(40.dp).width(40.dp)) {
            Image(
                painterResource(id = if (selected) imgActiv else img),
                contentDescription = stringResource(R.string.app_logo_desc),
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.tint(if (selected) colorResource(R.color.myOrange) else Color.Gray)
            )
        }
    }
}