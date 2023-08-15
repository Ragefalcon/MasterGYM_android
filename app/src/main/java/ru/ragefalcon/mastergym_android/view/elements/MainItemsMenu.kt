package ru.ragefalcon.mastergym_android.view.elements

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import ru.ragefalcon.mastergym_android.R

enum class MainItemsMenuOld (val item: BottomMenuItemOld) {
    Trainings(BottomMenuItemOld(label = "Тренировки", icon = Icons.Filled.DateRange)),
    Profile(BottomMenuItemOld(label = "Профиль", icon = Icons.Filled.Person));
}
enum class MainItemsMenu (val item: BottomMenuItem) {
    Trainings(BottomMenuItem( imgActiv = R.drawable.ic_girya_fill, img = R.drawable.ic_girya)),
    Profile(BottomMenuItem( imgActiv = R.drawable.ic_profile_gray_fill, img = R.drawable.ic_profile_gray));
}