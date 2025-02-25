package cn.straosp.workrecord.db

import cn.straosp.workrecord.bean.WorkRecord
import cn.straosp.workrecord.util.toISODateString
import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDate


interface WorkRecordEntity: Entity<WorkRecordEntity> {
    companion object: Entity.Factory<WorkRecordEntity>()
    val id: Int
    val teamSize: Int
    val singleProductQuantity: Double
    val singleProductPrice: Double
    val multipleProductQuantity: Double
    val multipleProductPrice: Double
    val workDate: LocalDate
    val accountId:Int
}
object WorkRecordTable: Table<WorkRecordEntity>("work_record") {
    val id = int("id").primaryKey().bindTo { it.id }
    val teamSize = int("team_size").bindTo { it.teamSize }
    val singleProductQuantity = double("single_product_quantity").bindTo { it.singleProductQuantity }
    val singleProductPrice = double("single_product_price").bindTo { it.singleProductPrice }
    val multipleProductQuantity = double("multiple_product_quantity").bindTo { it.multipleProductQuantity }
    val multipleProductPrice = double("multiple_product_price").bindTo { it.multipleProductPrice }
    val workDate = date("work_date").bindTo { it.workDate }
    val accountId = int("account_id").bindTo { it.accountId }
}
fun WorkRecordEntity.toWorkRecord(): WorkRecord = WorkRecord(
    id = id,
    teamSize = teamSize,
    singleProductQuantity = singleProductQuantity,
    singleProductPrice = singleProductPrice,
    multipleProductPrice = multipleProductPrice,
    multipleProductQuantity = multipleProductQuantity,
    workDate = workDate.toISODateString(),
    accountId = accountId
)
