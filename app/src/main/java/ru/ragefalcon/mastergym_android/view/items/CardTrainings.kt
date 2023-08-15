package ru.ragefalcon.mastergym_android.view.items

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import bdElement.BaseTraining
import ru.ragefalcon.mastergym_android.R


@Composable
fun CardTrainings(training: BaseTraining, onClick: (BaseTraining) -> Unit) {
    if (training.status == "second")
        CommonCardTrainings(
            training,
            smallC = colorResource(R.color.fontCommonColor),
            nameC = colorResource(R.color.my_color_font_header),
            opisC = colorResource(R.color.fontCommonColor),
            backC = colorResource(R.color.fontUnactiveColor),
            onClick = onClick
        )
    else
        CommonCardTrainings(
            training,
            smallC = colorResource(R.color.fontUnactiveColor),
            nameC = colorResource(R.color.fontHeaderColorInv),
            opisC = colorResource(R.color.fontUnactiveColor),
            backC = colorResource(R.color.myGreen),
            onClick = onClick
        )

}

