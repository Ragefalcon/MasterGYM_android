package ru.ragefalcon.mastergym_android.view.elements

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomMenuItemOld(val label: String, val icon: ImageVector)
data class BottomMenuItem(@DrawableRes val imgActiv: Int, @DrawableRes val img: Int)