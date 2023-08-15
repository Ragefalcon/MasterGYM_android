package ru.ragefalcon.mastergym_android.view.elements

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T : Any> MyList(
    list: List<T>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    reverse: Boolean = false,
    beforeListContent: @Composable (() -> Unit)? = null,
    comItem: @Composable (Int, T) -> Unit
) {
    if (list.isNotEmpty()) {
            LazyColumn(modifier= modifier, state = state, reverseLayout = reverse) {
                beforeListContent?.let {
                    item {
                        beforeListContent()
                    }
                }
                itemsIndexed(list) { ind, item ->
                    comItem(ind + if (beforeListContent != null) 1 else 0, item)
                }
            }
    } else {
        Spacer(modifier)
    }
}