package cn.straosp.workrecord.bean

import kotlinx.serialization.Serializable

@Serializable
data class AddWorkRecord(
    val teamSize: Int? = 0, // 团队人数
    val singleProductQuantity: Double = .0,
    val singleProductPrice: Double = .0,
    val multipleProductQuantity:Double = .0,
    val multipleProductPrice: Double = .0,
    val workDate: String, // 时间 2024-01-02
)

/**
 * @param teamSize 团队人数 可空
 * @param singleProductQuantity 单人产品数量
 * @param multipleProductQuantity 多人产品数量
 * @param singleProductPrice 单人产品单价
 * @param multipleProductPrice 多人产品单价
 * @param workDate 日期
 * @param accountId 提交人
 */
@Serializable
data class WorkRecord(
    val id: Int,
    val teamSize: Int = 0,
    val singleProductQuantity: Double,
    val singleProductPrice: Double,
    val multipleProductQuantity:Double,
    val multipleProductPrice: Double,
    val workDate: String,
    val accountId:Int
)

@Serializable
data class MonthWorkSummary(
    val workDate:String,
    val workingDays:Int,
    val totalSalary:Double,
    val totalMultipleProductQuantity:Double,
    val totalSingleProductQuantity:Double
)

@Serializable
data class YearWorkSummary(
    val startWorkDate:String,
    val endWorkDate:String,
    val workingDays:Int,
    val totalSalary:Double,
    val totalMultipleProductQuantity:Double,
    val totalSingleProductQuantity:Double
)

@Serializable
data class UpdateWorkRecord(
    val teamSize: Int,
    val singleProductQuantity: Double,
    val singleProductPrice: Double,
    val multipleProductQuantity:Double,
    val multipleProductPrice: Double,
    val workDate: String,
)