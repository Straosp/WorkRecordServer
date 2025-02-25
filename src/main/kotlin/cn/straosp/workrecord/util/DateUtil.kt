package cn.straosp.workrecord.util

import com.github.heqiao2010.lunar.LunarCalendar
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * 2024-05-01 To LocalDate
 */
fun String.toLocalDate(): LocalDate = if (this.isEmpty()) LocalDate.now() else LocalDate.parse(this, DateTimeFormatter.ISO_DATE)

fun LocalDate.toTimestampString(): String = this.toTimestamp().toString()

fun LocalDate.toTimestamp():Long {
    val localDateTime = this.atStartOfDay()
    return localDateTime.toEpochSecond(ZoneId.systemDefault().rules.getOffset(localDateTime)) * 1000L
}
fun LocalDate.toISODateString() = "${this.year}-${this.monthValue.toString().padStart(2,'0')}-${this.dayOfMonth.toString().padStart(2,'0')}"

private val dayOfMonth = intArrayOf(0,31,28,31,30,31,30,31,31,30,31,30,31)
fun Int.dayOfMonth(year:Int): Int {
    if (this == 2) return isLeapYearAndFebruaryDays(year).second
    if (this < 1 || this > 12) return 0
    return dayOfMonth[this]
}
fun isLeapYearAndFebruaryDays(year: Int): Pair<Boolean, Int> {
    val isLeapYear = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    val februaryDays = if (isLeapYear) 29 else 28
    return Pair(isLeapYear, februaryDays)
}

fun getCurrentMonthLocalDate():Pair<LocalDate,LocalDate> {
    val localDate = LocalDate.now(ZoneId.of("GMT+8"))
    val year = localDate.year
    val monthV = localDate.monthValue
    return  Pair(first = LocalDate.of(year,monthV, 1), second = LocalDate.of(year, monthV,monthV.dayOfMonth(year)))
}

fun getCurrentYear():Int {
    return  LocalDate.now().year
}
fun getCurrentMonth():Int {
    return  LocalDate.now().monthValue
}
fun LocalDate.toCalendar():Calendar{
    return Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("GMT+8"))).apply {
        set(this@toCalendar.year,this@toCalendar.monthValue,this@toCalendar.dayOfMonth)
    }
}
fun getLunarFirstDayToSolar(year:Int): LocalDate{
    val calendar = LunarCalendar.lunar2Solar(year,1,1, isLeapYearAndFebruaryDays(year).first)
    return LocalDate.of(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
}
fun getLunarLastDayToSolar(year:Int): LocalDate{
    val calendar = LunarCalendar.lunar2Solar(year + 1,1,1, isLeapYearAndFebruaryDays(year).first)
    return LocalDate.of(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
}

