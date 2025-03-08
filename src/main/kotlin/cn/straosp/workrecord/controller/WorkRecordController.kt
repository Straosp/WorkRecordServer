package cn.straosp.workrecord.controller

import cn.straosp.workrecord.bean.AddWorkRecord
import cn.straosp.workrecord.bean.UpdateWorkRecord
import cn.straosp.workrecord.plugin.getAccountId
import cn.straosp.workrecord.service.WorkRecordService
import cn.straosp.workrecord.util.Constant
import cn.straosp.workrecord.util.R
import cn.straosp.workrecord.util.RequestResult
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.workRecordController(){
    val workRecordService by application.inject<WorkRecordService>()
    route("/workRecord"){
        authenticate("auth") {
            post {
                val workRecord = runCatching { call.receive<AddWorkRecord>() }.getOrNull()
                workRecord?.let {
                    if (workRecord.teamSize == 0 && workRecord.singleProductQuantity == .0){
                     call.respond(R.parameterError("错误： teamSize == 0 AND singleProductQuantity == 0"))
                    }else if ((workRecord.teamSize ?: 0) > 0 && workRecord.multipleProductQuantity <= .0){
                        call.respond(R.parameterError("错误： teamSize > 0 AND multipleProductQuantity <= 0"))
                    }
                    when(val result = workRecordService.addWorkRecord(accountId = getAccountId(),workRecord = workRecord)){
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
            put("/{id}"){
                val workRecord = runCatching { call.receive<UpdateWorkRecord>() }.getOrNull()
                val id = runCatching { call.pathParameters["id"]?.toInt() }.getOrNull() ?: -1
                if (id == -1) {
                    call.respond(R.parameterError("workRecordId"))
                    return@put
                }
                workRecord?.let {
                    if (workRecord.teamSize == 0 && workRecord.singleProductQuantity == .0){
                        call.respond(R.parameterError("teamSize = 0 AND singleProductQuantity == 0"))
                    }else if ((workRecord.teamSize ?: 0) > 0 && (workRecord.multipleProductQuantity ?: .0) <= .0){
                        call.respond(R.parameterError("teamSize > 0 AND multipleProductQuantity <= 0"))
                    }else {
                        when(val result = workRecordService.updateWorkRecord(accountId = getAccountId(), workRecordId = id,workRecord = workRecord)){
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
            delete("/{id}") {
                val id = runCatching { call.pathParameters["id"]?.toInt() }.getOrNull() ?: -1
                if (id == -1) {
                    call.respond(R.parameterError("workRecordId"))
                    return@delete
                }
                workRecordService.deleteWorkRecord(accountId = getAccountId(), workRecordId = id)
                call.respond(R.success())
            }
            get("/{id}"){
                val workRecordIdRegex = Regex("\\d*")
                val workRecordId = call.pathParameters["id"] ?: ""
                if (!workRecordIdRegex.matches(workRecordId)){
                    call.respond(R.parameterError("workRecordId"))
                    return@get
                }
                call.respond(
                    R.success(data = workRecordService.getWorkRecordDetailById(accountId = getAccountId(), workRecordId = workRecordId.toInt()))
                )
            }
            get("/month/detail"){
                val monthRegex = Regex("\\d{1,2}")
                val yearRegex = Regex("\\d{4}")
                val year = call.queryParameters["year"] ?: ""
                val month = call.queryParameters["month"] ?: ""
                if (!monthRegex.matches(month) || !yearRegex.matches(year)){
                    call.respond(R.parameterError("年或月不合法"))
                    return@get
                }
                call.respond(R.success(data = workRecordService.getMonthWorkRecords(accountId = getAccountId(), year = year.toInt(),month = month.toInt())))
            }
            get("/month/summary"){
                val monthRegex = Regex("\\d{1,2}")
                val yearRegex = Regex("\\d{4}")
                val year = call.queryParameters["year"] ?: ""
                val month = call.queryParameters["month"] ?: ""
                if (!monthRegex.matches(month) || !yearRegex.matches(year)){
                    call.respond(R.parameterError("年或月不合法"))
                    return@get
                }
                call.respond(R.success(data = workRecordService.getMonthWorkSummary(accountId = getAccountId(), year = year.toInt(),month = month.toInt())))
            }
            get("lunar/detail"){
                val regex = Regex("[2-9][0-9]{3}")
                val year = call.queryParameters["year"] ?: ""
                if (!regex.matches(year)){
                    call.respond(R.parameterError("年不合法"))
                    return@get
                }
                call.respond(R.success(data = workRecordService.getLunarYearWorkRecords(accountId = getAccountId(),year = year.toInt())))

            }

        }
        authenticate("auth") {
            get("lunar/summary"){
                val principal = call.principal<JWTPrincipal>()
                println("Controller Claim: ${principal?.payload?.claims}")

                val regex = Regex("[2-9][0-9]{3}")
                val year = call.queryParameters["year"] ?: ""
                if (!regex.matches(year)){
                    call.respond(R.parameterError("年不合法"))
                    return@get
                }
                call.respond(R.success(data = workRecordService.getLunarYearWorkSummary(accountId = getAccountId(),year = year.toInt())))
            }
        }
    }
}