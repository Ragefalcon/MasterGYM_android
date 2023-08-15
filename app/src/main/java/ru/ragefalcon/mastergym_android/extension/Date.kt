package extension

import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDateTimeUTC(): Long = Date().time + TimeZone.getDefault().getOffset(Date().time)

fun convertUTCUnixDateToLocalTimeString(unixTime: Long, format: String = "dd.MM.yyyy"): String {
    val date = Date(unixTime)

    val dateFormat = SimpleDateFormat(format, Locale.getDefault())

    val timeZoneOffset = TimeZone.getDefault().getOffset(date.time)

    val localTime = date.time - timeZoneOffset

    return dateFormat.format(localTime)
}
