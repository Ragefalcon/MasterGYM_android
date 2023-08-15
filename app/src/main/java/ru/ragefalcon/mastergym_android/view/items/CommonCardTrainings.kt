package ru.ragefalcon.mastergym_android.view.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bdElement.BaseTraining
import coil.compose.AsyncImage
import coil.request.ImageRequest
import extension.convertUTCUnixDateToLocalTimeString
import extension.getCurrentDateTimeUTC
import ru.ragefalcon.mastergym_android.R
import ru.ragefalcon.mastergym_android.global.appConst
import ru.ragefalcon.mastergym_android.view.elements.CommonText
import ru.ragefalcon.mastergym_android.view.elements.SmallText
import ru.ragefalcon.mastergym_android.view.elements.SubheaderText
import kotlin.math.round

@Composable
fun CommonCardTrainings(
    training: BaseTraining,
    smallC: Color,
    nameC: Color,
    opisC: Color,
    backC: Color,
    onClick: (BaseTraining) -> Unit
) {
    val ripple = rememberRipple(bounded = false, color = LocalContentColor.current.copy(alpha = ContentAlpha.medium))

    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = appConst.paddingSmall, horizontal = appConst.paddingCommon)
            .background(backC, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple
            ) { onClick(training) }
            .padding(10.dp)
    ) {
        SrokAndCompleteBlock(training, smallC)
        SubheaderText(training.name ?: "", color = nameC)
        MuscleAndComplexBlock(training, opisC)
    }
}


@Composable
private fun SrokAndCompleteBlock(training: BaseTraining,smallC: Color){
    Row {
        SmallText(
            "${convertUTCUnixDateToLocalTimeString(training.date_open ?: 0)} " +
                    "- ${convertUTCUnixDateToLocalTimeString(training.date_close ?: 0)}" +
                    (round(((training.date_close ?: 0) - getCurrentDateTimeUTC()) / 8640000.0) / 10).let {
                        if (it < 0) "" else " (Ост: $it д.)"
                    },
            Modifier.weight(1f),
            color = smallC
        )
        training.completed?.let { compl ->
            if (compl > 10000000) {
                Icon(
                    painter = painterResource(id = R.drawable.round_check_circle_24),
                    contentDescription = "round check",
                    tint = colorResource(R.color.myOrange),
                    modifier = Modifier.width(20.dp).height(20.dp)
                )
                SmallText(convertUTCUnixDateToLocalTimeString(compl), color = smallC)
            }
        }
    }
}

@Composable
private fun MuscleAndComplexBlock(training: BaseTraining, opisC: Color){
    Row {
        (training.assignments.fold(mutableListOf<String>()) { str, assign ->
            assign.muscle_group_tags?.forEach {
                if (it != "") str.add(it)
            }
            str
        }.distinct().toString().let { it.substring(1, it.length - 1) }).let {
            CommonText(if (it == "") " --- " else it, Modifier.weight(1f), color = opisC)
        }
        if (training.complex_id != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(training.complex_cover ?: stringResource(R.string.temp_complex_cover_url)) //appImg.tempComplexCoverUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.app_logo),
                contentDescription = "complex cover",//stringResource(R.string.description),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp)),
                colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply {
                    setToSaturation(0f)
                })
            )
        }
    }
}