package cn.straosp.workrecord.util

import com.nlf.calendar.Lunar
import com.nlf.calendar.Solar
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * 2024-05-01 To LocalDate
 */
fun String.toLocalDate(): LocalDate =
    if (this.isEmpty()) LocalDate.now() else LocalDate.parse(this, DateTimeFormatter.ISO_DATE)

fun LocalDate.toTimestampString(): String = this.toTimestamp().toString()

fun LocalDate.toTimestamp(): Long {
    val localDateTime = this.atStartOfDay()
    return localDateTime.toEpochSecond(ZoneId.systemDefault().rules.getOffset(localDateTime)) * 1000L
}

fun LocalDate.toISODateString() =
    "${this.year}-${this.monthValue.toString().padStart(2, '0')}-${this.dayOfMonth.toString().padStart(2, '0')}"

fun LocalDate.toYearMonthDate() = "${this.year}-${this.monthValue.toString().padStart(2, '0')}"

private val dayOfMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
fun getDaysInMonth(yearMonth: YearMonth): Int {
    if (yearMonth.monthValue == 2) return isLeapYearAndFebruaryDays(yearMonth.year).second
    if (yearMonth.monthValue !in 1..12) return 0
    return dayOfMonth[yearMonth.monthValue]
}

fun isLeapYearAndFebruaryDays(year: Int): Pair<Boolean, Int> {
    val isLeapYear = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    val februaryDays = if (isLeapYear) 29 else 28
    return Pair(isLeapYear, februaryDays)
}

fun getCurrentMonthLocalDate(): Pair<LocalDate, LocalDate> {
    val localDate = LocalDate.now(ZoneId.of("GMT+8"))
    val year = localDate.year
    val monthV = localDate.monthValue
    return Pair(
        first = LocalDate.of(year, monthV, 1),
        second = LocalDate.of(year, monthV, getDaysInMonth(YearMonth.of(year, monthV)))
    )
}

fun getCurrentYear(): Int {
    return LocalDate.now().year
}

fun getCurrentMonth(): Int {
    return LocalDate.now().monthValue
}

fun getCurrentDay(): Int {
    return LocalDate.now().dayOfMonth
}

fun LocalDate.toCalendar(): Calendar {
    return Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("GMT+8"))).apply {
        set(this@toCalendar.year, this@toCalendar.monthValue, this@toCalendar.dayOfMonth)
    }
}

fun LocalDate.toSolar(): Solar {
    return Solar.fromYmd(year, monthValue, dayOfMonth)
}

fun Solar.toLocalDate(): LocalDate {
    return LocalDate.of(year, month, day)
}

fun LocalDate.getLunarMonth(): Int = this.toSolar().lunar.month

/**
 * 根据传过来的阳历时间获取农历新年第一天所对应的阳历时间
 * 比如说传过来的是 2026-01-02 今天是农历2025年的时间，则返回农历2025-01-01当天所对应的阳历时间
 * 如果农历时间的年与阳历时间的年相等，则返回本年的第一天时间
 */
fun getLunarFirstDayToSolar(year: Int): LocalDate {
    return Lunar.fromYmd(year,1,1).solar.toLocalDate()
}

/**
 * 根据传过来的阳历时间判断是否那一年的时间
 * 比如说今天是 2026-01-02，是属于农历2025年的，则返回农历时间2026-12-31所对应的阳历时间
 * 如果说2025年过去了，如2026-04-03,这个时间已经是农历2026年了，则返回农历2026-12-31所对应的阳历时间
 * 由于农历的12月最后一天不固定，可能只有29天，所以统一返回为下一年的第一天，也就是 2026-01-01所对应的阳历时间
 * 使用的位置判断时间的时候需要注意
 */
fun getLunarLastDayToSolar(year: Int): LocalDate {
    return Lunar.fromYmd(year + 1,1,1).solar.toLocalDate().minusDays(1)
}

