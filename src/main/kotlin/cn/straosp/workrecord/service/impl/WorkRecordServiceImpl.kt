package cn.straosp.workrecord.service.impl

import cn.straosp.workrecord.bean.*
import cn.straosp.workrecord.db.AppDatabase
import cn.straosp.workrecord.db.WorkRecordTable
import cn.straosp.workrecord.service.WorkRecordService
import cn.straosp.workrecord.util.*
import org.ktorm.dsl.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDate

class WorkRecordServiceImpl : WorkRecordService {

    override fun addWorkRecord(accountId: Int, workRecord: AddWorkRecord): RequestResult<Boolean> {
        val wr = AppDatabase.database.from(WorkRecordTable).select()
            .where {
                (WorkRecordTable.workDate eq workRecord.workDate.toLocalDate()) and ( WorkRecordTable.accountId eq  accountId)
            }.map { row ->
                row[WorkRecordTable.id] ?: 0
            }.toList() ?: emptyList()
        if (wr.isNotEmpty()){
            return RequestResult.Failure(RequestErrorMessage(Constant.INSERT_WORK_RECORD_FAILED_CODE,Constant.INSERT_WORK_RECORD_FAILED_MESSAGE))
        }
        AppDatabase.database.insert(WorkRecordTable){
            set(WorkRecordTable.workDate,workRecord.workDate.toLocalDate())
            set(WorkRecordTable.teamSize,workRecord.teamSize)
            set(WorkRecordTable.singleProductPrice,workRecord.singleProductPrice)
            set(WorkRecordTable.singleProductQuantity,workRecord.singleProductQuantity)
            set(WorkRecordTable.multipleProductPrice,workRecord.multipleProductPrice)
            set(WorkRecordTable.multipleProductQuantity,workRecord.multipleProductQuantity)
            set(WorkRecordTable.accountId,accountId)
        }
        return RequestResult.Success(true)
    }

    override fun addWorkRecords(accountId: Int, workRecords: List<AddWorkRecord>): RequestResult<Boolean> {
        AppDatabase.database.useTransaction {
            AppDatabase.database.batchInsert(WorkRecordTable){
                workRecords.forEach { wr ->
                    item {
                        set(WorkRecordTable.workDate,wr.workDate.toLocalDate())
                        set(WorkRecordTable.teamSize,wr.teamSize)
                        set(WorkRecordTable.singleProductPrice,wr.singleProductPrice)
                        set(WorkRecordTable.singleProductQuantity,wr.singleProductQuantity)
                        set(WorkRecordTable.multipleProductPrice,wr.multipleProductPrice)
                        set(WorkRecordTable.multipleProductQuantity,wr.multipleProductQuantity)
                    }
                }
            }
        }
        return RequestResult.Success(true)
    }

    override fun getMonthWorkRecords(accountId: Int,year:Int, month: Int): List<WorkRecord> {
        val startLocalDate = LocalDate.of(year,month,1)
        val endLocalDate = LocalDate.of(year,month,month.dayOfMonth(year))
        val result = AppDatabase.database.from(WorkRecordTable).select().where {
            WorkRecordTable.workDate.between(LocalDateRange(startLocalDate,endLocalDate)) and   (WorkRecordTable.accountId eq accountId)
        }.map { row ->
            WorkRecord(
                id = row[WorkRecordTable.id] ?: 0,
                teamSize = row[WorkRecordTable.teamSize] ?: 0,
                singleProductPrice = row[WorkRecordTable.singleProductPrice] ?: .0,
                singleProductQuantity = row[WorkRecordTable.singleProductQuantity] ?: .0,
                multipleProductPrice = row[WorkRecordTable.multipleProductPrice] ?: .0,
                multipleProductQuantity = row[WorkRecordTable.multipleProductQuantity] ?: .0,
                workDate = (row[WorkRecordTable.workDate])?.toISODateString() ?: "",
                accountId = row[WorkRecordTable.accountId] ?: 0
            )
        }
        return result
    }

    override fun getMonthWorkSummary(accountId: Int,year:Int, month: Int): MonthWorkSummary {
        val startLocalDate = LocalDate.of(year,month,1)
        val endLocalDate = LocalDate.of(year,month,month.dayOfMonth(year))
        val result = AppDatabase.database.from(WorkRecordTable).select().where {
            WorkRecordTable.workDate.between(LocalDateRange(startLocalDate,endLocalDate)) and   (WorkRecordTable.accountId eq accountId)
        }.map { row ->
            WorkRecord(
                id = row[WorkRecordTable.id] ?: 0,
                teamSize = row[WorkRecordTable.teamSize] ?: 0,
                singleProductPrice = row[WorkRecordTable.singleProductPrice] ?: .0,
                singleProductQuantity = row[WorkRecordTable.singleProductQuantity] ?: .0,
                multipleProductPrice = row[WorkRecordTable.multipleProductPrice] ?: .0,
                multipleProductQuantity = row[WorkRecordTable.multipleProductQuantity] ?: .0,
                workDate = (row[WorkRecordTable.workDate])?.toISODateString() ?: "",
                accountId = row[WorkRecordTable.accountId] ?: 0
            )
        }
        val decimalFormat = DecimalFormat("#.##").apply {
            roundingMode = RoundingMode.DOWN
        }
        val salary = result.groupBy { it.teamSize }.map { (key, workRecords) ->
            if (key == 0){
                workRecords.sumOf { (it.singleProductPrice * it.singleProductQuantity)}
            }else{
                key?.let {
                    workRecords.sumOf { (it.singleProductPrice * it.singleProductQuantity) + (it.multipleProductPrice.times(it.multipleProductQuantity).div(key)) }
                }
            }
        }.sumOf { it ?: .0 }
        return MonthWorkSummary(
            workDate = startLocalDate.toYearMonthDate(),
            workingDays = result.size,
            totalSalary = decimalFormat.format(salary).toDouble(),
            totalSingleProductQuantity = result.sumOf { it.singleProductQuantity },
            totalMultipleProductQuantity = result.sumOf { it.multipleProductQuantity }
        )
    }

    override fun getLunarYearWorkRecords(accountId: Int, year: Int): List<WorkRecord> {
        val startLocalDate = getLunarFirstDayToSolar(year)
        val endLocalDate = getLunarLastDayToSolar(year)
        val result = AppDatabase.database.from(WorkRecordTable).select().where {
            WorkRecordTable.workDate.between(LocalDateInterval(startLocalDate,endLocalDate)) and   (WorkRecordTable.accountId eq accountId)
        }.map { row ->
            WorkRecord(
                id = row[WorkRecordTable.id] ?: 0,
                teamSize = row[WorkRecordTable.teamSize] ?: 0,
                singleProductPrice = row[WorkRecordTable.singleProductPrice] ?: .0,
                singleProductQuantity = row[WorkRecordTable.singleProductQuantity] ?: .0,
                multipleProductPrice = row[WorkRecordTable.multipleProductPrice] ?: .0,
                multipleProductQuantity = row[WorkRecordTable.multipleProductQuantity] ?: .0,
                workDate = (row[WorkRecordTable.workDate])?.toISODateString() ?: "",
                accountId = row[WorkRecordTable.accountId] ?: 0
            )
        }
        return result
    }

    override fun getLunarYearWorkSummary(accountId: Int, year: Int): YearWorkSummary {
        val startLocalDate = getLunarFirstDayToSolar(year)
        val endLocalDate = getLunarLastDayToSolar(year)
        val result = AppDatabase.database.from(WorkRecordTable).select().where {
            WorkRecordTable.workDate.between(LocalDateInterval(startLocalDate,endLocalDate)) and   (WorkRecordTable.accountId eq accountId)
        }.map { row ->
            WorkRecord(
                id = row[WorkRecordTable.id] ?: 0,
                teamSize = row[WorkRecordTable.teamSize] ?: 0,
                singleProductPrice = row[WorkRecordTable.singleProductPrice] ?: .0,
                singleProductQuantity = row[WorkRecordTable.singleProductQuantity] ?: .0,
                multipleProductPrice = row[WorkRecordTable.multipleProductPrice] ?: .0,
                multipleProductQuantity = row[WorkRecordTable.multipleProductQuantity] ?: .0,
                workDate = (row[WorkRecordTable.workDate])?.toISODateString() ?: "",
                accountId = row[WorkRecordTable.accountId] ?: 0
            )
        }
        val decimalFormat = DecimalFormat("#.##").apply {
            roundingMode = RoundingMode.DOWN
        }
        val salary = result.groupBy { it.teamSize }.map { (key, workRecords) ->
            if (key == 0){
                workRecords.sumOf { (it.singleProductPrice * it.singleProductQuantity)}
            }else{
                key?.let {
                    workRecords.sumOf { (it.singleProductPrice * it.singleProductQuantity) + (it.multipleProductPrice.times(it.multipleProductQuantity).div(key)) }
                }
            }
        }.sumOf { it ?: .0 }
        return YearWorkSummary(
            startWorkDate = startLocalDate.toISODateString(),
            endWorkDate = endLocalDate.toISODateString(),
            workingDays = result.size,
            totalSalary = decimalFormat.format(salary).toDouble(),
            totalSingleProductQuantity = result.sumOf { it.singleProductQuantity },
            totalMultipleProductQuantity = result.sumOf { it.multipleProductQuantity }
        )

    }

    override fun getWorkRecordDetailById(accountId: Int, workRecordId: Int): WorkRecord? {
        val result = AppDatabase.database.from(WorkRecordTable).select()
            .where {
                (WorkRecordTable.accountId eq accountId) and (WorkRecordTable.id eq workRecordId)
            }
            .map { row ->
                WorkRecord(
                    id = row[WorkRecordTable.id] ?: 0,
                    teamSize = row[WorkRecordTable.teamSize] ?: 0,
                    singleProductPrice = row[WorkRecordTable.singleProductPrice] ?: .0,
                    singleProductQuantity = row[WorkRecordTable.singleProductQuantity] ?: .0,
                    multipleProductPrice = row[WorkRecordTable.multipleProductPrice] ?: .0,
                    multipleProductQuantity = row[WorkRecordTable.multipleProductQuantity] ?: .0,
                    workDate = (row[WorkRecordTable.workDate])?.toISODateString() ?: "",
                    accountId = row[WorkRecordTable.accountId] ?: 0
                )
            }
        return if (result.isNullOrEmpty()) null else result.first()
    }

    override fun updateWorkRecord(accountId: Int, workRecordId:Int,workRecord: UpdateWorkRecord): RequestResult<Boolean> {
        val wr = AppDatabase.database.from(WorkRecordTable).select()
            .where {
                (WorkRecordTable.accountId eq accountId) and (WorkRecordTable.workDate eq workRecord.workDate.toLocalDate())
            }.map { row ->
                row[WorkRecordTable.id] ?: 0
            }.toList() ?: emptyList()
        if (wr.isNotEmpty() && wr.first() != workRecordId){
            return RequestResult.Failure(RequestErrorMessage(Constant.UPDATE_WORK_RECORD_FAILED_CODE,Constant.UPDATE_WORK_RECORD_FAILED_MESSAGE))
        }
        AppDatabase.database.update(WorkRecordTable){
            set(WorkRecordTable.workDate,workRecord.workDate.toLocalDate())
            set(WorkRecordTable.teamSize,workRecord.teamSize)
            set(WorkRecordTable.singleProductPrice,workRecord.singleProductPrice)
            set(WorkRecordTable.singleProductQuantity,workRecord.singleProductQuantity)
            set(WorkRecordTable.multipleProductPrice,workRecord.multipleProductPrice)
            set(WorkRecordTable.multipleProductQuantity,workRecord.multipleProductQuantity)
            where {
                ( WorkRecordTable.id eq workRecordId ) and  (WorkRecordTable.accountId eq  accountId)
            }
        }
        return RequestResult.Success(true)
    }

    override fun deleteWorkRecord(accountId: Int, workRecordId: Int) {
        AppDatabase.database.delete(WorkRecordTable){
            (WorkRecordTable.id eq workRecordId) and (WorkRecordTable.accountId eq accountId)
        }
    }


}