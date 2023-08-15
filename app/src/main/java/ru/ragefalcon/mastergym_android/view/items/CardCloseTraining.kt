package ru.ragefalcon.mastergym_android.view.items

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import bdElement.BaseTraining
import ru.ragefalcon.mastergym_android.R

@Composable
fun CardCloseTraining(training: BaseTraining) {
    CommonCardTrainings(
        training,
        smallC = colorResource(R.color.fontCommonColor),
        nameC = colorResource(R.color.fontCommonColor),
        opisC = colorResource(R.color.fontCommonColor),
        backC = colorResource(R.color.fontUnactiveColor),
        onClick = {}
    )
}