package cn.straosp.workrecord.service

import cn.straosp.workrecord.bean.*
import cn.straosp.workrecord.util.RequestResult

interface WorkRecordService {

    fun addWorkRecord(accountId:Int, workRecord: AddWorkRecord): RequestResult
    fun addWorkRecords(accountId:Int, workRecords: List<AddWorkRecord>): Result<Boolean>
    fun getMonthWorkRecords(accountId: Int,month: Int): List<WorkRecord>
    fun getMonthWorkSummary(accountId: Int,month: Int): MonthWorkSummary
    fun getLunarYearWorkRecords(accountId: Int,year: Int): List<WorkRecord>
    fun getLunarYearWorkSummary(accountId: Int,year: Int): YearWorkSummary

    fun updateWorkRecord(accountId: Int,workRecordId:Int,workRecord: UpdateWorkRecord): RequestResult
    fun deleteWorkRecord(accountId: Int,workRecordId: Int)

}