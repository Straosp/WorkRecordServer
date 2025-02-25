package cn.straosp.workrecord.service

import cn.straosp.workrecord.bean.AddWorkRecord
import cn.straosp.workrecord.bean.WorkRecord
import cn.straosp.workrecord.bean.YearWorkSummary

interface WorkRecordService {

    fun addWorkRecord(accountId:Int, workRecord: AddWorkRecord): Result<Boolean>
    fun addWorkRecords(accountId:Int, workRecords: List<AddWorkRecord>): Result<Boolean>
    fun getMonthWorkRecords(accountId: Int,month: Int): List<WorkRecord>
    fun getLunarYearWorkRecords(accountId: Int,year: Int): List<WorkRecord>
    fun getLunarYearWorkSummary(accountId: Int,year: Int): YearWorkSummary

    fun updateWorkRecord(accountId: Int,workRecordId:Int,workRecord: WorkRecord): Result<Boolean>
    fun deleteWorkRecord(accountId: Int,workRecordId: Int)

}