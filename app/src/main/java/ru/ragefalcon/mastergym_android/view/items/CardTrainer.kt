package ru.ragefalcon.mastergym_android.view.items

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bdElement.BaseClient
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.ragefalcon.mastergym_android.R
import ru.ragefalcon.mastergym_android.global.appConst
import ru.ragefalcon.mastergym_android.view.elements.CommonText
import ru.ragefalcon.mastergym_android.view.elements.SubheaderText

@Composable
fun CardTrainer(trainer: BaseClient) {
    Row(
        Modifier
            .padding(vertical = appConst.paddingSmall, horizontal = appConst.paddingCommon),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(trainer.avatar ?: stringResource(R.string.temp_avatar_url))
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.app_logo),
            contentDescription = "avatar trainer",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(70.dp)
                .height(70.dp)
                .clip(CircleShape)
        )
        Column(Modifier.padding(start = appConst.paddingSmall).weight(1f), verticalArrangement = Arrangement.Center) {
            SubheaderText(trainer.name ?: "")
            trainer.email?.let { email -> CommonText(email, color = colorResource(R.color.fontCommonColor)) }
            trainer.phone?.let { phone -> CommonText(phone, color = colorResource(R.color.fontCommonColor)) }
            trainer.status?.let { status ->
                if (status != "open") when (status) {
                    "new" -> stringResource(R.string.status_trainer_new)
                    "rejected" -> stringResource(R.string.status_trainer_rejected)
                    "canceled" -> stringResource(R.string.status_trainer_canceled)
                    "close" -> stringResource(R.string.status_trainer_close)
                    else -> stringResource(R.string.status_trainer_open)
                }.let {
                    CommonText(
                        it,
                        color = colorResource(R.color.fontCommonColor),
                        modifier = Modifier
                            .border(1.dp, colorResource(R.color.fontCommonColor),RoundedCornerShape(5.dp))
                            .padding(10.dp,3.dp)
                    )
                }
            }
        }
    }
}