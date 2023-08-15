package bdElement

import kotlinx.serialization.Serializable

@Serializable
data class ComplexInfo(
    val complex: BaseComplex,
    val count_trainings: Long,
    val srok: Long,
    val max_date: Long,
    val date_opens: List<Long>,
    val started: Boolean,
    val request: Boolean
){
}