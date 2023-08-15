package ru.ragefalcon.mastergym_android.view.pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import bdElement.BaseAssignment
import bdElement.BaseTraining
import extension.getCurrentDateTimeUTC
import kotlinx.coroutines.launch
import ru.ragefalcon.mastergym_android.R
import ru.ragefalcon.mastergym_android.global.appConst
import ru.ragefalcon.mastergym_android.view.elements.*
import ru.ragefalcon.mastergym_android.view.extensions.getPreview
import ru.ragefalcon.mastergym_android.viewmodel.MainViewModel

@Composable
fun PageSelectedTraining(vm: MainViewModel, training: BaseTraining) {

    BackHandler(true) {
        vm.selectedTraining = null
    }

    val listAssignment = CommonOpenItemPanel<BaseAssignment>(
        whenOpen = { _ -> },
        openedItem = { assignment, startOpenAnimation, _, finishAnimation ->
            OpenVideoOnFullscreen(assignment, startOpenAnimation, finishAnimation)
        },
        mainSpis = { list, modifierList, selection, openSpisIndexItem, lazyListState ->
            MainListAssignmentsWithOpis(vm, training, list, modifierList, selection, openSpisIndexItem, lazyListState)
        }
    )

    Column(Modifier.fillMaxWidth()) {
        listAssignment.Show(training.assignments, Modifier.fillMaxWidth())
    }

}

@Composable
private fun OpenVideoOnFullscreen(
    assignment: BaseAssignment,
    startOpenAnimation: MutableState<Boolean>,
    finishAnimation: State<Boolean>
) {
    BackHandler(true) {
        startOpenAnimation.value = false
    }
    Box(modifier = Modifier, contentAlignment = Alignment.Center) {
        VideoPlayer(
            "https://vz-d6d857fb-fb1.b-cdn.net/${assignment.bunny_guid}/play_720p.mp4",
            if ((finishAnimation.value && startOpenAnimation.value).not()) Modifier.padding(10.dp)
                .clip(RoundedCornerShape(15.dp)) else Modifier.fillMaxSize()
        ) {
            startOpenAnimation.value = false
        }
        if ((finishAnimation.value && startOpenAnimation.value).not()) {
            assignment.getPreview(
                Modifier.padding(10.dp).fillMaxSize()
                    .clip(RoundedCornerShape(15.dp))
                    .clickable {
                        startOpenAnimation.value = false
                    }
            )
        }
    }

}

@Composable
private fun ColumnScope.MainListAssignmentsWithOpis(
    vm: MainViewModel,
    training: BaseTraining,
    list: List<BaseAssignment>,
    modifierList: Modifier,
    selection: SingleSelectionType<BaseAssignment>,
    openSpis_Index_Item: (Int, BaseAssignment, Rect?) -> Unit,
    lazyListState: LazyListState
) {
    val ripple = rememberRipple(bounded = true, color = LocalContentColor.current.copy(alpha = ContentAlpha.medium))
    var enableCompleteButt by remember { mutableStateOf(true) }

    MyList(
        list.sortedBy { it.order },
        Modifier.padding(appConst.paddingCommon).weight(1f).then(modifierList),
        lazyListState,
        beforeListContent = {
            training.name?.let { SubheaderText(it, color = colorResource(R.color.myGreen)) }
            training.description?.let { TextHtml(it) }
        }) { ind, item ->
        ItemAssignment(ind, item, selection, openSpis_Index_Item, ripple)
    }

    Row {
        Button({ vm.selectedTraining = null }, Modifier.padding(10.dp).weight(1f)) {
            Text(stringResource(R.string.label_butt_back))
        }
        Button(
            {
                enableCompleteButt = false
                vm.viewModelScope.launch {
                    vm.func.client.setCompletedTrainingFirestore(training, getCurrentDateTimeUTC()).let {
                        if (it) {
                            vm.date.openTrainingsForClient.update()
                            vm.selectedTraining = null
                        }
                    }
                }
            }, Modifier.padding(10.dp).weight(1f),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.myOrange),
                contentColor = Color.White
            ),
            enabled = enableCompleteButt
        ) {
            Text(stringResource(R.string.labal_butt_complete_training))
        }
    }
}

@Composable
private fun ItemAssignment(
    ind: Int, item: BaseAssignment, selection: SingleSelectionType<BaseAssignment>,
    openSpis_Index_Item: (Int, BaseAssignment, Rect?) -> Unit,
    ripple: Indication
) {
    Column(Modifier.fillMaxWidth()) {
        item.name?.let { CommonText(it, color = colorResource(R.color.myBlue)) }
        item.description?.let { TextHtml(it) }
        var tmp: Rect? = null
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            item.getPreview(Modifier
                .onGloballyPositioned {
                    it.parentCoordinates?.boundsInParent()?.let { parentRect ->
                        it.boundsInParent().let { childRect ->
                            tmp = Rect(parentRect.topLeft + childRect.topLeft, childRect.size)
                        }
                    }
                }
                .padding(10.dp)
                .width(210.dp)
                .height(280.dp)
                .clip(RoundedCornerShape(15.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple
                ) {
                    selection.selected = item
                    openSpis_Index_Item(ind, item, tmp)
                }
            )
            Image(
                modifier = Modifier.width(160.dp).height(160.dp).alpha(0.7f),
                contentScale = ContentScale.FillBounds,
                painter = painterResource(id = androidx.media3.ui.R.drawable.exo_icon_play),
                contentDescription = "Enter/Exit fullscreen"
            )
        }
    }
}