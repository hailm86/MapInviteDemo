package com.hailm.mapinvitedemo.base.extension

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

fun ZonedDateTime.toDateMonth(): String {
    val formatter =
        DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH)
            .withZone(ZoneOffset.systemDefault())
    return format(formatter)
}

fun ZonedDateTime.toMonthDate(): String {
    val formatter =
        DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH).withZone(ZoneOffset.systemDefault())
    return format(formatter)
}

fun ZonedDateTime.toDayDateMonth(): String {
    val formatter = DateTimeFormatter.ofPattern("E, d MMMM", Locale.ENGLISH)
        .withZone(ZoneOffset.systemDefault())
    return format(formatter)
}

fun ZonedDateTime.dateOfBirth(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
    return format(formatter)
}

fun ZonedDateTime.isToday(): Boolean {
    val formatter = DateTimeFormatter.ofPattern("d MM yyyy", Locale.ENGLISH).withZone(
        ZoneOffset.systemDefault()
    )
    return this.toLocalDateTime().format(formatter) == LocalDateTime.now().format(formatter)
}
