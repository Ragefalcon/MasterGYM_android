package bdElement

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class ParamCommonSpisRequest @OptIn(ExperimentalSerializationApi::class) constructor(
    @EncodeDefault
//    @SerialName("limit")
    val limit: Int = 0,
    @EncodeDefault
//    @SerialName("skip")
    val skip: Int = 0
)

