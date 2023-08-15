package bdElement

import kotlinx.serialization.Serializable

@Serializable
data class CommonPageList<T>(
    val rows: List<T>? = null,
    val totalCount: Int? = null
) {
}