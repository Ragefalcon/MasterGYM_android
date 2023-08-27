package ru.ragefalcon.mastergym_android.view.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.ragefalcon.mastergym_android.R
import ru.ragefalcon.mastergym_android.global.appConst
import ru.ragefalcon.mastergym_android.view.LoaderComponent
import ru.ragefalcon.mastergym_android.view.elements.HeaderText
import ru.ragefalcon.mastergym_android.view.elements.SubheaderText
import ru.ragefalcon.mastergym_android.view.elements.paginationElement
import ru.ragefalcon.mastergym_android.view.items.CardCloseTraining
import ru.ragefalcon.mastergym_android.view.items.CardTrainer
import ru.ragefalcon.mastergym_android.view.items.CardTrainings
import ru.ragefalcon.mastergym_android.viewmodel.MainViewModel
import ru.ragefalcon.mastergym_android.viewmodel.helpers.SpisPageLoaded

@Composable
fun PageTrainings(vm: MainViewModel) {

    val trainingsOp by vm.date.openTrainingsForClient.spisLoaded.collectAsState()
    val loadTrainingsOp by vm.date.openTrainingsForClient.loadingProcess.collectAsState()
    val trainingsOld by vm.date.oldTrainingsForClient.spisLoaded.collectAsState()
    val loadTrainingsOld by vm.date.oldTrainingsForClient.loadingProcess.collectAsState()
    val trainers by vm.date.listTrainersOpen.spisLoaded.collectAsState()
    val loadTrainer by vm.date.listTrainersOpen.loadingProcess.collectAsState()
    val trainersClose by vm.date.listTrainersClose.spisLoaded.collectAsState()
    val loadTrainerClose by vm.date.listTrainersClose.loadingProcess.collectAsState()

    LazyColumn(Modifier) {
        item {
            CommonHeader(
                Modifier
                    .padding(top = appConst.paddingCommon)
                    .padding(horizontal = appConst.paddingCommon),
                vm.archiveTrainer,
                if (vm.date.listTrainersOpen.totalCount > 1) stringResource(R.string.header_your_trainers) else stringResource(
                    R.string.header_your_trainer
                ),
                stringResource(R.string.header_archive_trainers),
                vm.date.listTrainersClose.totalCount > 0
            ) {
                vm.archiveTrainer = vm.archiveTrainer.not()
            }
        }

        if (vm.archiveTrainer.not()) CommonItems(vm.date.listTrainersOpen, trainers, loadTrainer, true) {
            CardTrainer(it)
        } else CommonItems(vm.date.listTrainersClose, trainersClose, loadTrainerClose, true) {
            CardTrainer(it)
        }

        item {
            CommonHeader(
                Modifier
                    .padding(horizontal = appConst.paddingCommon),
                vm.archiveTrainings,
                stringResource(R.string.header_open_trainings),
                stringResource(R.string.header_close_trainings),
                vm.date.oldTrainingsForClient.totalCount > 0
            ) {
                vm.archiveTrainings = vm.archiveTrainings.not()
            }
        }

        if (vm.archiveTrainings.not()) CommonItems(vm.date.openTrainingsForClient, trainingsOp, loadTrainingsOp) {
            CardTrainings(it) { trainFromCard ->
                vm.selectedTraining = trainFromCard
            }
        }
        else CommonItems(vm.date.oldTrainingsForClient, trainingsOld, loadTrainingsOld) {
            CardCloseTraining(it)
        }
        item {
            Box(Modifier.fillMaxWidth().height(appConst.paddingCommon))
        }
    }
}


@Composable
private fun CommonHeader(modifier: Modifier, archive: Boolean, text: String, textArchive: String, enableArchiveButt: Boolean, onClick: () -> Unit) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        HeaderText(
            if (archive) textArchive else text,
            color = colorResource(R.color.myGreen),
            modifier = Modifier.weight(1f)
        )
        if (enableArchiveButt) Image(
            painter = painterResource(if (archive) R.drawable.ic_archive_blue else R.drawable.ic_archive_gray),
            contentDescription = "Image from Internet",
            modifier = Modifier
                .width(30.dp)
                .height(30.dp)
                .aspectRatio(1f)
                .clickable { onClick() }
        )
    }
}

private fun <T> LazyListScope.CommonItems(
    spisPage: SpisPageLoaded<T>,
    trainers: List<T>,
    loadingProcess: Boolean,
    delimeter: Boolean = false,
    card: @Composable (T) -> Unit
) {
    if (loadingProcess) item {
        LoaderComponent()
    } else {
        if (trainers.isNotEmpty()) {
            itemsIndexed(trainers) { ind, item ->
                if (ind != 0 && delimeter) Box(
                    Modifier
                        .padding(horizontal = appConst.paddingCommon)
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(colorResource(R.color.fontUnactiveColor))
                )
                card(item)
            }
            item {
                paginationElement(
                    Modifier
                        .padding(horizontal = appConst.paddingCommon),
                    spisPage
                )
            }
        } else {
            item {
                SubheaderText(
                    stringResource(spisPage.emptyMessage),
                    Modifier.padding(horizontal = appConst.paddingCommon),
                    color = colorResource(R.color.fontCommonColor)
                )
            }
        }
    }
}