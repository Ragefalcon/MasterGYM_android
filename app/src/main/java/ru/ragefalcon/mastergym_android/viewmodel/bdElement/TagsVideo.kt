package bdElement

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable

@Serializable
class TagsVideo(
    var sex_id: List<String> = listOf(),
    var equipment_id: List<String> = listOf(),
    var muscle_group_id: List<String> = listOf()
) {
    var equipment_tags_state by mutableStateOf(arrayListOf<Pair<MyTags, MutableState<Boolean>>>())
    var muscle_group_tags_state by mutableStateOf(arrayListOf<Pair<MyTags, MutableState<Boolean>>>())
    var sex_tags_state by mutableStateOf(arrayListOf<Pair<MyTags, MutableState<Boolean>>>())

    fun clearBlock(){
        equipment_tags_state.forEach {
            it.second.value = false
        }
        muscle_group_tags_state.forEach {
            it.second.value = false
        }
        sex_tags_state.forEach {
            it.second.value = false
        }
    }

    fun clear() {
        sex_id = listOf()
        equipment_id = listOf()
        muscle_group_id = listOf()
        clearBlock()
    }
    fun isEmpty(): Boolean = sex_id.isEmpty() && equipment_id.isEmpty() && muscle_group_id.isEmpty()
    fun isNotEmpty(): Boolean = isEmpty().not()
}
