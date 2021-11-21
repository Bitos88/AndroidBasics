package io.keepcoding.chat.common

import java.text.SimpleDateFormat
import java.util.*

class DateManager {
    companion object {
        fun getCurrentDate(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat( "dd.MM.yyyy HH:mm")
            return format.format(date)
        }
    }
}