package cn.straosp.workrecord.bean

import cn.straosp.workrecord.util.R
import cn.straosp.workrecord.util.getCurrentDay
import cn.straosp.workrecord.util.getCurrentMonth
import cn.straosp.workrecord.util.getCurrentYear
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class AddWorkRecord(
    val teamSize: Int? = 0, // 团队人数
    val singleProductQuantity: Double = .0,
    val singleProductPrice: Double = .0,
    val multipleProductQuantity: Double = .0,
    val multipleProductPrice: Double = .0,
    val workDate: String, // 时间 2024-01-02
)

/**
 * 统计接口统一的请求参数
 * @param isLunar 是否为农历时间，请求的时间是否为农历时间，主要是在前端实现是否全程农历功能
 * @param year 年
 * @param month 月
 * @param day 日
 */
@Serializable
data class Summary(
    val isLunar: Boolean = false,
    val year: Int = getCurrentYear(),
    val month: Int = getCurrentMonth(),
    val day: Int = getCurrentDay(),
)

/**
 * get 请求参数转 summary [Summary]
 */
suspend fun RoutingCall.readSummary(
    defaultIsLunar: Boolean = false,
    defaultYear: Int = getCurrentYear(),
    defaultMonth: Int = getCurrentMonth(),
    defaultDay: Int = getCurrentDay(),
): Summary {

    val year = (queryParameters["year"] ?: "0").toInt()
    if (year < 2020 || year > getCurrentYear()) {
        respond(R.parameterError("year must be between 1 and 2020"))
    }
    return Summary(
        isLunar = if (queryParameters.contains("isLunar")) queryParameters["isLunar"] == "1" else defaultIsLunar,
        year = (queryParameters["year"] ?: "").toIntOrNull() ?: defaultYear,
        month = (queryParameters["month"] ?: "").toIntOrNull() ?: defaultMonth,
        day = (queryParameters["day"] ?: "").toIntOrNull() ?: defaultDay,
    )
}


/**
 * @param teamSize 团队人数 可空
 * @param singleProductQuantity 单人产品数量
 * @param multipleProductQuantity 多人产品数量
 * @param singleProductPrice 单人产品单价
 * @param multipleProductPrice 多人产品单价
 * @param workDate 日期
 */
@Serializable
data class WorkRecord(
    val id: Int,
    val teamSize: Int = 0,
    val singleProductQuantity: Double,
    val singleProductPrice: Double,
    val multipleProductQuantity: Double,
    val multipleProductPrice: Double,
    val workDate: String
)

/**
 * 年度总结
 * @param startDate 年度第一天(非工作的第一天)
 * @param endDate 年度最后一天(非工作的最后一天)
 * @param workingDays 年工作天数
 * @param totalSalary 年度总工资
 * @param totalMultipleProductQuantity 年度总团队件数
 * @param totalSingleProductQuantity 年度总个人件数
 */
@Serializable
data class YearlySummary(
    val startDate: String,
    val endDate: String,
    val workingDays: Int,
    val totalSalary: Double,
    val totalMultipleProductQuantity: Double,
    val totalSingleProductQuantity: Double
)

/**
 * 更新工作记录
 * @param teamSize 团队人数
 * @param singleProductPrice 个人产品单价
 * @param singleProductQuantity 个人件数
 * @param multipleProductQuantity 团队件数
 * @param multipleProductPrice 团队产品单价
 */
@Serializable
data class UpdateWorkRecord(
    val teamSize: Int,
    val singleProductQuantity: Double,
    val singleProductPrice: Double,
    val multipleProductQuantity: Double,
    val multipleProductPrice: Double,
    val workDate: String,
)