package cn.straosp.workrecord.controller

import cn.straosp.workrecord.bean.AddWorkRecord
import cn.straosp.workrecord.bean.UpdateWorkRecord
import cn.straosp.workrecord.plugin.getAccountId
import cn.straosp.workrecord.service.WorkRecordService
import cn.straosp.workrecord.util.R
import cn.straosp.workrecord.util.RequestResult
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.workRecordController(){
    val workRecordService by application.inject<WorkRecordService>()
    route("/workRecord"){
        authenticate {
            post {
                val workRecord = runCatching { call.receive<AddWorkRecord>() }.getOrNull()
                workRecord?.let {
                    if (workRecord.teamSize == 0 && workRecord.singleProductQuantity == .0){
                     call.respond(R.parameterError())
                    }else if ((workRecord.teamSize ?: 0) > 0 && workRecord.multipleProductQuantity <= .0){
                        call.respond(R.parameterError())
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
                call.respond(R.parameterError())
            }
            put("/{id}"){
                val workRecord = runCatching { call.receive<UpdateWorkRecord>() }.getOrNull()
                val id = runCatching { call.pathParameters["id"]?.toInt() }.getOrNull() ?: -1
                if (id == -1) {
                    call.respond(R.parameterError())
                    return@put
                }
                workRecord?.let {
                    if (workRecord.teamSize == 0 && workRecord.singleProductQuantity == .0){
                        call.respond(R.parameterError())
                    }else if ((workRecord.teamSize ?: 0) > 0 && (workRecord.multipleProductQuantity ?: .0) <= .0){
                        call.respond(R.parameterError())
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
                call.respond(R.parameterError())
            }
            delete("/{id}") {
                val id = runCatching { call.pathParameters["id"]?.toInt() }.getOrNull() ?: -1
                if (id == -1) {
                    call.respond(R.parameterError())
                    return@delete
                }
                workRecordService.deleteWorkRecord(accountId = getAccountId(), workRecordId = id)
                call.respond(R.success())
            }
            get("/{month}"){
                val regex = Regex("\\d{1,2}")
                val requestType = call.queryParameters["type"]
                val month = call.pathParameters["month"] ?: ""
                if (!regex.matches(month)){
                    call.respond(R.parameterError())
                    return@get
                }
                if (requestType.isNullOrEmpty() || requestType == "detail"){
                    call.respond(R.success(data = workRecordService.getMonthWorkRecords(accountId = getAccountId(),month = month.toInt())))
                }else if (requestType.isNotEmpty() && requestType == "summary" ){
                    call.respond(R.success(data = workRecordService.getMonthWorkSummary(accountId = getAccountId(),month = month.toInt())))
                }
            }
            get("lunar/{year}"){
                val regex = Regex("[2-9][0-9]{3}")
                val requestType = call.queryParameters["type"]
                val year = call.pathParameters["year"] ?: ""
                if (!regex.matches(year)){
                    call.respond(R.parameterError())
                    return@get
                }
                if (requestType.isNullOrEmpty() || requestType == "detail"){
                    call.respond(R.success(data = workRecordService.getLunarYearWorkRecords(accountId = getAccountId(),year = year.toInt())))
                }else if (requestType.isNotEmpty() && requestType == "summary" ){
                    call.respond(R.success(data = workRecordService.getLunarYearWorkSummary(accountId = getAccountId(),year = year.toInt())))
                }
            }
        }
    }
}