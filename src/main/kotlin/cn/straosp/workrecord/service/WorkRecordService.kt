package cn.straosp.workrecord.service

import cn.straosp.workrecord.bean.*
import cn.straosp.workrecord.util.RequestResult

interface WorkRecordService {

    fun addWorkRecord(accountId:Int, workRecord: AddWorkRecord): RequestResult<Boolean>
    fun addWorkRecords(accountId:Int, workRecords: List<AddWorkRecord>): RequestResult<Boolean>
    fun getMonthWorkRecords(accountId: Int,year:Int,month: Int): List<WorkRecord>
    fun getMonthWorkSummary(accountId: Int,year:Int,month: Int): MonthWorkSummary
    fun getLunarYearWorkRecords(accountId: Int,year: Int): List<WorkRecord>
    fun getLunarYearWorkSummary(accountId: Int,year: Int): YearWorkSummary
    fun getWorkRecordDetailById(accountId: Int,workRecordId: Int):WorkRecord?
    fun updateWorkRecord(accountId: Int,workRecordId:Int,workRecord: UpdateWorkRecord): RequestResult<Boolean>
    fun deleteWorkRecord(accountId: Int,workRecordId: Int)

}