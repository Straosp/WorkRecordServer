package cn.straosp.workrecord.service

import cn.straosp.workrecord.bean.*
import cn.straosp.workrecord.util.RequestResult
import java.time.LocalDate

interface WorkRecordService {

    /**
     * 添加工作记录
     * @param accountId 用户id
     * @param workRecord 工作记录内容 [AddWorkRecord]
     */
    fun addWorkRecord(accountId:Int, workRecord: AddWorkRecord): RequestResult<Boolean>

    /**
     * 批量添加工作记录
     * @param accountId 用户id
     * @param workRecords 工作记录集合 [AddWorkRecord]
     */
    fun addWorkRecords(accountId:Int, workRecords: List<AddWorkRecord>): RequestResult<Boolean>

    /**
     * 根据id获取工作记录详情
     * @param accountId 用户id
     * @param workRecordId 工作记录id
     */
    fun getWorkRecordDetailById(accountId: Int,workRecordId: Int):WorkRecord?

    /**
     * 根据id更新工作记录内容
     * @param accountId 用户id
     * @param workRecordId 工作记录id
     * @param workRecord 工作记录内容
     */
    fun updateWorkRecord(accountId: Int,workRecordId:Int,workRecord: UpdateWorkRecord): RequestResult<Boolean>

    /**
     * 删除一条工作记录
     */
    fun deleteWorkRecord(accountId: Int,workRecordId: Int)

    /**
     * 根据年月查询此月份的工作记录
     */
    fun getMonthWorkRecords(accountId: Int,summary: Summary): List<WorkRecord>

    /**
     * 根据年月查询每个月的总结情况，用于前端显示柱状图
     */
    fun getSummaryInYearMonth(accountId: Int,summary: Summary): List<YearlySummary>

    /**
     * 获取年度工作总结或者月度工作总结
     */
    fun getYearlyMonthlySummary(accountId: Int,summary: Summary): YearlySummary

}