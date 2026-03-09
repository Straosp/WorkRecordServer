package cn.straosp.workrecord.controller

import cn.straosp.workrecord.bean.AddWorkRecord
import cn.straosp.workrecord.bean.UpdateWorkRecord
import cn.straosp.workrecord.bean.readSummary
import cn.straosp.workrecord.plugin.getAccountId
import cn.straosp.workrecord.service.WorkRecordService
import cn.straosp.workrecord.util.R
import cn.straosp.workrecord.util.RequestResult
import cn.straosp.workrecord.util.getCurrentMonthLocalDate
import cn.straosp.workrecord.util.getCurrentYear
import cn.straosp.workrecord.util.toISODateString
import cn.straosp.workrecord.util.toLocalDate
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDate

fun Routing.workRecordController() {
    val workRecordService by application.inject<WorkRecordService>()
    authenticate("auth") {
        route("/workRecord") {
            /**
             * 添加新的工作记录
             */
            post {
                val workRecord = runCatching { call.receive<AddWorkRecord>() }.getOrNull()
                workRecord?.let {
                    if (workRecord.teamSize == 0 && workRecord.singleProductQuantity == .0) {
                        call.respond(R.parameterError("错误： teamSize == 0 AND singleProductQuantity == 0"))
                    } else if ((workRecord.teamSize ?: 0) > 0 && workRecord.multipleProductQuantity <= .0) {
                        call.respond(R.parameterError("错误： teamSize > 0 AND multipleProductQuantity <= 0"))
                    }
                    when (val result =
                        workRecordService.addWorkRecord(accountId = getAccountId(), workRecord = workRecord)) {
                        is RequestResult.Success<*> -> {
                            call.respond(R.success())
                        }

                        is RequestResult.Failure -> {
                            call.respond(R.error(result.errorMessage))
                        }
                    }
                    return@post
                }
                call.respond(R.parameterError("JSON To AddWorkRecord Failed"))
            }
            /**
             * 修改工作记录
             */
            put("/{id}") {
                val workRecord = runCatching { call.receive<UpdateWorkRecord>() }.getOrNull()
                val workRecordId = (call.pathParameters["id"] ?: "").toIntOrNull() ?: -1
                if (workRecordId < 0) {
                    call.respond(R.parameterError("workRecordId"))
                    return@put
                }
                workRecord?.let {
                    if (workRecord.teamSize == 0 && workRecord.singleProductQuantity == .0) {
                        call.respond(R.parameterError("teamSize = 0 AND singleProductQuantity == 0"))
                    } else if ((workRecord.teamSize ?: 0) > 0 && (workRecord.multipleProductQuantity ?: .0) <= .0) {
                        call.respond(R.parameterError("teamSize > 0 AND multipleProductQuantity <= 0"))
                    } else {
                        when (val result = workRecordService.updateWorkRecord(
                            accountId = getAccountId(),
                            workRecordId = workRecordId,
                            workRecord = workRecord
                        )) {
                            is RequestResult.Success<*> -> {
                                call.respond(R.success())
                            }

                            is RequestResult.Failure -> {
                                call.respond(R.error(result.errorMessage))
                            }
                        }
                    }
                    return@put
                }
                call.respond(R.parameterError("JSON To UpdateWorkRecord Failed"))
            }
            /**
             * 删除工作记录
             */
            delete("/{id}") {
                val id = runCatching { call.pathParameters["id"]?.toInt() }.getOrNull() ?: -1
                if (id == -1) {
                    call.respond(R.parameterError("workRecordId"))
                    return@delete
                }
                workRecordService.deleteWorkRecord(accountId = getAccountId(), workRecordId = id)
                call.respond(R.success())
            }
            /**
             * 查询id的工作记录详情内容
             */
            get("/{id}") {
                val workRecordIdRegex = Regex("\\d*")
                val workRecordId = call.pathParameters["id"] ?: ""
                if (!workRecordIdRegex.matches(workRecordId)) {
                    call.respond(R.parameterError("workRecordId"))
                    return@get
                }
                call.respond(
                    R.success(
                        data = workRecordService.getWorkRecordDetailById(
                            accountId = getAccountId(),
                            workRecordId = workRecordId.toInt()
                        )
                    )
                )
            }
            /**
             * 通过参数的年月查询月记录信息，返回月内每天的工作记录情况
             */
            get {
                val summary = call.readSummary()
                val result = workRecordService.getMonthWorkRecords(accountId = getAccountId(), summary = summary)
                call.respond(R.success(data = result))
            }
            get("/monthly") {
                val summary = call.readSummary()
                val result = workRecordService.getSummaryInYearMonth(accountId = getAccountId(), summary = summary)
                call.respond(R.success(data = result))
            }
            /**
             * 月度总结，年度总结
             */
            get("/summary") {
                val summary = call.readSummary(defaultMonth = 0, defaultDay = 0)
                val result = workRecordService.getYearlyMonthlySummary(accountId = getAccountId(), summary = summary)
                call.respond(R.success(data = result))
            }


        }
    }

}