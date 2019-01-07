package com.ferbajoo.timedream.core.utils

import java.util.concurrent.TimeUnit

/**
 * method to convert millisecond to time format
 *
 * @param milliSeconds
 * @return HH:mm:ss time formatted string
 */
fun hmsTimeFormatter(milliSeconds: Long): String {
    return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(milliSeconds),
            TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)))
}

fun secoundTimeFormatter(milliSeconds: Long): String {
    return String.format("%02d",
            TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)))
}
