package com.example.lookers.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun formatTimeAgo(dateTimeString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val pastTime = LocalDateTime.parse(dateTimeString.split(".")[0], formatter)
    val now = LocalDateTime.now()

    val years = ChronoUnit.YEARS.between(pastTime, now)
    if (years > 0) return "${years}년 전"

    val months = ChronoUnit.MONTHS.between(pastTime, now)
    if (months > 0) return "${months}달 전"

    val days = ChronoUnit.DAYS.between(pastTime, now)
    if (days > 0) return "${days}일 전"

    val hours = ChronoUnit.HOURS.between(pastTime, now)
    if (hours > 0) return "${hours}시간 전"

    val minutes = ChronoUnit.MINUTES.between(pastTime, now)
    if (minutes > 0) return "${minutes}분 전"

    val seconds = ChronoUnit.SECONDS.between(pastTime, now)
    return if (seconds > 10) "${seconds}초 전" else "최근"
}

fun convertTime(dateTimeString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val pastTime = LocalDateTime.parse(dateTimeString.split(".")[0], formatter)
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")

    return pastTime.format(outputFormatter)
}

fun convertTimeToKorean(dateTimeString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val pastTime = LocalDateTime.parse(dateTimeString.split(".")[0], formatter)

    val year = pastTime.year
    val month = pastTime.monthValue
    val day = pastTime.dayOfMonth

    return "${year}년 ${month}월 ${day}일"
}
