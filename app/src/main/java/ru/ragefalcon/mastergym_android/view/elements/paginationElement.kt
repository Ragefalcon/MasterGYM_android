package ru.ragefalcon.mastergym_android.view.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import ru.ragefalcon.mastergym_android.R
import ru.ragefalcon.mastergym_android.global.appConst
import ru.ragefalcon.mastergym_android.viewmodel.helpers.SpisPageLoaded

private val sizeEl = 30.dp

@Composable
private fun <T> massivePage(start: Int, end: Int, spisPL: SpisPageLoaded<T>) {
    for (i in start..end) {
        Box(
            modifier = Modifier
                .padding(start = if (i!=1) appConst.paddingVerySmall else 0.dp)
                .width(sizeEl).height(sizeEl)
                .background(
                    if (i == spisPL.currentPage + 1) colorResource(R.color.myGreen) else Color.White,
                    shape = CircleShape
                ).run {
                    if (i != spisPL.currentPage + 1) border(1.dp, colorResource(R.color.fontUnactiveColor), shape = CircleShape)
                    else this
                }
                .clip(CircleShape)
                .clickable {
                    spisPL.setCurrPage(i - 1)
                },
            contentAlignment = Alignment.Center
        ) {
            CommonText(
                "$i",
                color = if (i == spisPL.currentPage + 1) Color.White else colorResource(R.color.fontCommonColor)
            )
        }
    }
}


@Composable
private fun spacePoint() {
    Box(
        modifier = Modifier
            .padding(start = appConst.paddingVerySmall)
            .width(sizeEl).height(sizeEl),
        contentAlignment = Alignment.Center
    ) {
        CommonText("...", color = colorResource(R.color.my_color_font_header))
    }
}

private val sosed = 1 //if (phoneDevice) 1 else 2

@Composable
private fun <T> mainElem(modifier: Modifier = Modifier, spisPL: SpisPageLoaded<T>) {
    Row(modifier) {
        if (spisPL.totalPage < sosed * 2 + 5) massivePage(
            1,
            spisPL.totalPage,
            spisPL
        ) else when (spisPL.currentPage + 1) {
            in 1..sosed + 3 -> {
                massivePage(1, spisPL.currentPage + sosed + 1, spisPL)
                spacePoint()
                massivePage(spisPL.totalPage, spisPL.totalPage, spisPL)
            }

            in sosed + 4..spisPL.totalPage - (sosed + 3) -> {
                massivePage(1, 1, spisPL)
                spacePoint()
                massivePage(spisPL.currentPage - (sosed - 1), spisPL.currentPage + sosed + 1, spisPL)
                spacePoint()
                massivePage(spisPL.totalPage, spisPL.totalPage, spisPL)
            }

            in spisPL.totalPage - (sosed + 3)..spisPL.totalPage -> {
                massivePage(1, 1, spisPL)
                spacePoint()
                massivePage(spisPL.currentPage - (sosed - 1), spisPL.totalPage, spisPL)
            }
        }
    }
}

@Composable
fun <T> paginationElement(modifier: Modifier = Modifier, spisPL: SpisPageLoaded<T>) {
    if (spisPL.totalPage > 1) mainElem(modifier, spisPL)
}
