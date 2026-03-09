package cn.straosp.workrecord.service.impl

import cn.straosp.workrecord.bean.*
import cn.straosp.workrecord.db.AccountTable
import cn.straosp.workrecord.db.AppDatabase
import cn.straosp.workrecord.db.WorkRecordTable
import cn.straosp.workrecord.service.WorkRecordService
import cn.straosp.workrecord.util.*
import com.nlf.calendar.Lunar
import org.ktorm.dsl.*
import java.time.LocalDate
import java.time.YearMonth

class WorkRecordServiceImpl : WorkRecordService {

    /**
     * 添加工作记录
     * @param accountId 用户id
     * @param workRecord 工作记录内容 [AddWorkRecord]
     */
    override fun addWorkRecord(
        accountId: Int, workRecord: AddWorkRecord
    ): RequestResult<Boolean> {
        val wr = AppDatabase.database.from(WorkRecordTable).select().where {
            (WorkRecordTable.workDate eq workRecord.workDate.toLocalDate()) and (WorkRecordTable.accountId eq accountId)
        }.map { row ->
            row[WorkRecordTable.id] ?: 0
        }.toList() ?: emptyList()
        if (wr.isNotEmpty()) {
            return RequestResult.Failure(
                RequestErrorMessage(
                    Constant.INSERT_WORK_RECORD_FAILED_CODE, Constant.INSERT_WORK_RECORD_FAILED_MESSAGE
                )
            )
        }
        AppDatabase.database.insert(WorkRecordTable) {
            set(WorkRecordTable.workDate, workRecord.workDate.toLocalDate())
            set(WorkRecordTable.teamSize, workRecord.teamSize)
            set(WorkRecordTable.singleProductPrice, workRecord.singleProductPrice)
            set(WorkRecordTable.singleProductQuantity, workRecord.singleProductQuantity)
            set(WorkRecordTable.multipleProductPrice, workRecord.multipleProductPrice)
            set(WorkRecordTable.multipleProductQuantity, workRecord.multipleProductQuantity)
            set(WorkRecordTable.accountId, accountId)
        }
        return RequestResult.Success(true)
    }

    /**
     * 批量添加工作记录
     * @param accountId 用户id
     * @param workRecords 工作记录集合 [AddWorkRecord]
     */
    override fun addWorkRecords(
        accountId: Int, workRecords: List<AddWorkRecord>
    ): RequestResult<Boolean> {
        AppDatabase.database.useTransaction {
            AppDatabase.database.batchInsert(WorkRecordTable) {
                workRecords.forEach { wr ->
                    item {
                        set(WorkRecordTable.workDate, wr.workDate.toLocalDate())
                        set(WorkRecordTable.teamSize, wr.teamSize)
                        set(WorkRecordTable.singleProductPrice, wr.singleProductPrice)
                        set(WorkRecordTable.singleProductQuantity, wr.singleProductQuantity)
                        set(WorkRecordTable.multipleProductPrice, wr.multipleProductPrice)
                        set(WorkRecordTable.multipleProductQuantity, wr.multipleProductQuantity)
                    }
                }
            }
        }
        return RequestResult.Success(true)
    }

    /**
     * 根据id获取工作记录详情
     * @param accountId 用户id
     * @param workRecordId 工作记录id
     */
    override fun getWorkRecordDetailById(
        accountId: Int, workRecordId: Int
    ): WorkRecord? {
        val result = AppDatabase.database.from(WorkRecordTable).select().where {
            (WorkRecordTable.accountId eq accountId) and (WorkRecordTable.id eq workRecordId)
        }.map { row ->
            WorkRecord(
                id = row[WorkRecordTable.id] ?: 0,
                teamSize = row[WorkRecordTable.teamSize] ?: 0,
                singleProductPrice = row[WorkRecordTable.singleProductPrice] ?: .0,
                singleProductQuantity = row[WorkRecordTable.singleProductQuantity] ?: .0,
                multipleProductPrice = row[WorkRecordTable.multipleProductPrice] ?: .0,
                multipleProductQuantity = row[WorkRecordTable.multipleProductQuantity] ?: .0,
                workDate = (row[WorkRecordTable.workDate])?.toISODateString() ?: "",
            )
        }
        return if (result.isEmpty()) null else result.first()
    }

    /**
     * 根据id更新工作记录内容
     * @param accountId 用户id
     * @param workRecordId 工作记录id
     * @param workRecord 工作记录内容
     */
    override fun updateWorkRecord(
        accountId: Int, workRecordId: Int, workRecord: UpdateWorkRecord
    ): RequestResult<Boolean> {
        val wr = AppDatabase.database.from(WorkRecordTable).select().where {
            (WorkRecordTable.accountId eq accountId) and (WorkRecordTable.workDate eq workRecord.workDate.toLocalDate())
        }.map { row ->
            row[WorkRecordTable.id] ?: 0
        }.toList() ?: emptyList()
        if (wr.isNotEmpty() && wr.first() != workRecordId) {
            return RequestResult.Failure(
                RequestErrorMessage(
                    Constant.UPDATE_WORK_RECORD_FAILED_CODE, Constant.UPDATE_WORK_RECORD_FAILED_MESSAGE
                )
            )
        }
        AppDatabase.database.update(WorkRecordTable) {
            set(WorkRecordTable.workDate, workRecord.workDate.toLocalDate())
            set(WorkRecordTable.teamSize, workRecord.teamSize)
            set(WorkRecordTable.singleProductPrice, workRecord.singleProductPrice)
            set(WorkRecordTable.singleProductQuantity, workRecord.singleProductQuantity)
            set(WorkRecordTable.multipleProductPrice, workRecord.multipleProductPrice)
            set(WorkRecordTable.multipleProductQuantity, workRecord.multipleProductQuantity)
            where {
                (WorkRecordTable.id eq workRecordId) and (WorkRecordTable.accountId eq accountId)
            }
        }
        return RequestResult.Success(true)
    }


    override fun deleteWorkRecord(accountId: Int, workRecordId: Int) {
        AppDatabase.database.delete(WorkRecordTable) {
            (WorkRecordTable.id eq workRecordId) and (WorkRecordTable.accountId eq accountId)
        }
    }

    /**
     * 根据年月查询此月份的工作记录
     */
    override fun getMonthWorkRecords(
        accountId: Int, summary: Summary
    ): List<WorkRecord> {
        val startLocalDate = LocalDate.of(summary.year, summary.month, 1)
        val endLocalDate = LocalDate.of(
            summary.year, summary.month, getDaysInMonth(YearMonth.of(summary.year, summary.month))
        )
        val result = AppDatabase.database.from(WorkRecordTable).select().where {
            WorkRecordTable.workDate.between(
                LocalDateRange(
                    startLocalDate, endLocalDate
                )
            ) and (WorkRecordTable.accountId eq accountId)
        }.orderBy(WorkRecordTable.workDate.desc()).map { row ->
            WorkRecord(
                id = row[WorkRecordTable.id] ?: 0,
                teamSize = row[WorkRecordTable.teamSize] ?: 0,
                singleProductPrice = row[WorkRecordTable.singleProductPrice] ?: .0,
                singleProductQuantity = row[WorkRecordTable.singleProductQuantity] ?: .0,
                multipleProductPrice = row[WorkRecordTable.multipleProductPrice] ?: .0,
                multipleProductQuantity = row[WorkRecordTable.multipleProductQuantity] ?: .0,
                workDate = (row[WorkRecordTable.workDate])?.toISODateString() ?: "",
            )
        }
        return result
    }

    /**
     * 根据年月查询每个月的总结情况，用于前端显示柱状图
     */
    override fun getSummaryInYearMonth(
        accountId: Int,
        summary: Summary
    ): List<YearlySummary> {
        val startDate = if (summary.isLunar) {
            Lunar.fromYmd(summary.year, 1, 1).solar.toLocalDate()
        } else {
            LocalDate.of(summary.year, 1, 1)
        }
        val endDate = if (summary.isLunar) {
            Lunar.fromYmd(summary.year + 1, 1, 1).solar.toLocalDate().minusDays(1)
        } else {
            LocalDate.of(summary.year, 12, 31)
        }
        val result = AppDatabase.database.from(WorkRecordTable)
            .innerJoin(AccountTable, on = WorkRecordTable.accountId eq AccountTable.id).select().where {
                AccountTable.id eq accountId and WorkRecordTable.workDate.between(
                    LocalDateRange(
                        startDate, endDate
                    )
                )
            }.map { row ->
                WorkRecord(
                    id = row[WorkRecordTable.id] as Int,
                    teamSize = row[WorkRecordTable.teamSize] as Int,
                    singleProductPrice = row[WorkRecordTable.singleProductPrice] as Double,
                    singleProductQuantity = row[WorkRecordTable.singleProductQuantity] as Double,
                    multipleProductPrice = row[WorkRecordTable.multipleProductPrice] as Double,
                    multipleProductQuantity = row[WorkRecordTable.multipleProductQuantity] as Double,
                    workDate = (row[WorkRecordTable.workDate])?.toISODateString() ?: "",
                )
            }
        return result.groupBy { it.workDate.toLocalDate().monthValue }.map { entry ->
            val totalSalary = entry.value.sumOf { wk ->
                if (wk.teamSize == 0) {
                    wk.singleProductPrice * wk.singleProductQuantity
                } else {
                    wk.singleProductPrice * wk.singleProductQuantity + (wk.multipleProductPrice * wk.multipleProductQuantity).div(
                        wk.teamSize
                    )
                }
            }
            val totalSingleQuantity = entry.value.sumOf { wk -> wk.singleProductQuantity }
            val totalMultiProductQuantity =
                entry.value.sumOf { wk -> if (wk.teamSize == 0) .0 else wk.multipleProductQuantity }


            YearlySummary(
                startDate = entry.value.first().workDate.toLocalDate().toYearMonthDate(),
                endDate = entry.value.first().workDate.toLocalDate().toYearMonthDate(),
                workingDays = entry.value.size,
                totalSalary = totalSalary,
                totalMultipleProductQuantity = totalMultiProductQuantity,
                totalSingleProductQuantity = totalSingleQuantity
            )
        }


    }

    /**
     * 获取年度工作总结或者月度工作总结
     */
    override fun getYearlyMonthlySummary(
        accountId: Int,
        summary: Summary
    ): YearlySummary {
        val (startDate, endDate) = calcLocalDateRange(summary)
        println("startDate: $startDate endDate: $endDate")
        val result = AppDatabase.database.from(WorkRecordTable)
            .innerJoin(AccountTable, on = WorkRecordTable.accountId eq AccountTable.id).select().where {
                AccountTable.id eq accountId and WorkRecordTable.workDate.between(
                    LocalDateRange(
                        startDate, endDate
                    )
                )
            }.map { row ->
                WorkRecord(
                    id = row[WorkRecordTable.id] as Int,
                    teamSize = row[WorkRecordTable.teamSize] as Int,
                    singleProductPrice = row[WorkRecordTable.singleProductPrice] as Double,
                    singleProductQuantity = row[WorkRecordTable.singleProductQuantity] as Double,
                    multipleProductPrice = row[WorkRecordTable.multipleProductPrice] as Double,
                    multipleProductQuantity = row[WorkRecordTable.multipleProductQuantity] as Double,
                    workDate = (row[WorkRecordTable.workDate])?.toISODateString() ?: "",
                )
            }

        val totalSalary = result.sumOf { wk ->
            if (wk.teamSize == 0) {
                wk.singleProductPrice * wk.singleProductQuantity
            } else {
                wk.singleProductPrice * wk.singleProductQuantity + (wk.multipleProductPrice * wk.multipleProductQuantity).div(
                    wk.teamSize
                )
            }
        }
        val totalSingleQuantity = result.sumOf { wk -> wk.singleProductQuantity }
        val totalMultiProductQuantity =
            result.sumOf { wk -> if (wk.teamSize == 0) .0 else wk.multipleProductQuantity.div(wk.teamSize) }

        return YearlySummary(
            startDate = startDate.toISODateString(),
            endDate = endDate.toISODateString(),
            workingDays = result.size,
            totalSalary = totalSalary,
            totalMultipleProductQuantity = totalMultiProductQuantity,
            totalSingleProductQuantity = totalSingleQuantity
        )
    }

    /**
     * 计算年度月度总结的起至时间
     * 如果是农历，则起至时间也是由农历转公历查询，返回的结果就是公历
     * [Summary.isLunar] 如果为true则代表传过来的时间为农历时间
     */
    private fun calcLocalDateRange(summary: Summary): Pair<LocalDate, LocalDate> {
        if (summary.month == 0) {
            val startDate = if (summary.isLunar) {
                getLunarFirstDayToSolar(summary.year)
            } else {
                LocalDate.of(summary.year, 1, 1)
            }
            val endDate = if (summary.isLunar) {
                getLunarLastDayToSolar(summary.year)
            } else {
                LocalDate.of(summary.year, 12, getDaysInMonth(YearMonth.of(summary.year, 12)))
            }
            return Pair(startDate, endDate)
        } else {
            val startDate = if (summary.isLunar) {
                Lunar.fromYmd(summary.year, summary.month, 1).solar.toLocalDate()
            } else {
                LocalDate.of(summary.year, summary.month, 1)
            }
            val endDate = if (summary.isLunar) {
                if (summary.month == 12) {
                    Lunar.fromYmd(summary.year + 1, 1, 1).solar.toLocalDate()
                } else {
                    Lunar.fromYmd(summary.year, summary.month + 1, 1).solar.toLocalDate()
                }
            } else {
                LocalDate.of(summary.year, summary.month, getDaysInMonth(YearMonth.of(summary.year, summary.month)))
            }
            return Pair(startDate, endDate)
        }
    }


}