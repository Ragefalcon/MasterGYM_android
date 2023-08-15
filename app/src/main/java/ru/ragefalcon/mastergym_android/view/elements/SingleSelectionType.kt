package ru.ragefalcon.mastergym_android.view.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class SingleSelectionType<T : Any>() {
    private var selectedMut: MutableState<T?> = mutableStateOf(null)
    var selected: T?
        get() = selectedMut.value
        set(value) {selectedMut.value = value}

    @Composable
    fun launchedEffect(listener: (T?) -> Unit){
        LaunchedEffect(selectedMut.value) {
            listener(selectedMut.value)
        }
    }

    fun <T : Any> isActive(item: T): Boolean {
        selected?.let {
            if (it::class == item::class && it == item) return true
        }
        return false
    }
}