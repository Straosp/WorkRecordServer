package cn.straosp.workrecord.controller

import cn.straosp.workrecord.bean.Login
import cn.straosp.workrecord.bean.Token
import cn.straosp.workrecord.bean.UpdatePassword
import cn.straosp.workrecord.service.AccountService
import cn.straosp.workrecord.util.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun Routing.accountController(){
    val accountService by application.inject<AccountService>()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    route("/account"){
        post("/login"){
            val login = runCatching { call.receive<Login>() }.getOrNull()
            val result = accountService.verifyAccount(login?.phone ?: "",login?.password ?: "")
            when(result){
                is RequestResult.Success -> {
                    val localDateTime = Date.from(LocalDateTime.now(ZoneId.systemDefault()).plusMonths(1).atZone(ZoneId.systemDefault()).toInstant())
                    println("Account: ${result.data}")
                    val token = JWT.create()
                        .withAudience(jwtAudience)
                        .withIssuer(jwtIssuer)
                        .withClaim(Constant.TOKEN_CLAIM_PHONE_KEY, result.data.phone)
                        .withClaim(Constant.TOKEN_CLAIM_PASSWORD_KEY, result.data.password)
                        .withClaim(Constant.TOKEN_CLAIM_ACCOUNT_ID_KEY, result.data.id)
                        .withExpiresAt(localDateTime)
                        .sign(Algorithm.HMAC256(jwtSecret))
                    call.respond(R.success(data = Token(token)))
                }
                is RequestResult.Failure -> {
                    call.respond(R.error(Constant.ACCOUNT_LOGIN_FAILED_CODE,Constant.ACCOUNT_LOGIN_FAILED_MESSAGE))
                }
            }
        }
        post("/register"){
            val login = runCatching { call.receive<Login>() }.getOrNull()
            if (null == login || login.phone.isEmpty() || login.password.isEmpty()){
                call.respond(R.parameterError("phone","password"))
                return@post
            }
            when(val result = accountService.registerAccount(login.phone, login.password)){
                is RequestResult.Success -> {
                    val localDateTime = Date.from(LocalDateTime.now(ZoneId.systemDefault()).plusMonths(1).atZone(ZoneId.systemDefault()).toInstant())
                    val token = JWT.create()
                        .withAudience(jwtAudience)
                        .withIssuer(jwtIssuer)
                        .withClaim(Constant.TOKEN_CLAIM_PHONE_KEY, result.data.phone)
                        .withClaim(Constant.TOKEN_CLAIM_PASSWORD_KEY, result.data.password)
                        .withClaim(Constant.TOKEN_CLAIM_ACCOUNT_ID_KEY, result.data.id)
                        .withExpiresAt(localDateTime)
                        .sign(Algorithm.HMAC256(jwtSecret))
                    call.respond(R.success(data = Token(token)))
                }
                is RequestResult.Failure -> {
                    call.respond(result.errorMessage.toR())
                }
            }
        }
        authenticate("auth") {
            post("/{accountId}/avatar"){
                val accountId = call.pathParameters["accountId"] ?: ""
                val regex = Regex("\\d*")
                if (!regex.matches(accountId)){
                    call.respond(R.parameterError("accountId"))
                    return@post
                }
                val multiplatform = runCatching { call.receiveMultipart() }.getOrNull()
                if (null == multiplatform){
                    call.respond(R.error("未收到图片"))
                    return@post
                }
                multiplatform.forEachPart { data ->
                    when (data) {
                        is PartData.FormItem -> {}
                        is PartData.FileItem -> {
                            data.originalFileName?.let {fileName ->
                                val ext = File(fileName).extension
                                val saveFileName = "header_${System.currentTimeMillis()}_${accountId}.$ext"
                                SaveFileUtil.getInstance().saveHeaderFile(saveFileName,data.provider)
                                when (val result = accountService.uploadHeader(accountId.toInt(),saveFileName)){
                                    is RequestResult.Success -> {
                                        call.respond(R.success())
                                    }
                                    is RequestResult.Failure -> {
                                        call.respond(R.error(result.errorMessage.message ?: "数据异常，请稍后重试"))
                                    }
                                }
                            }
                        }
                        else -> {
                            call.respond(R.error("不支持的格式"))
                        }
                    }
                    data.dispose()
                }
                call.respond(R.error())
            }
            put("/{accountId}/password"){
                val accountId = call.pathParameters["accountId"] ?: ""
                val regex = Regex("\\d*")
                if (!regex.matches(accountId)){
                    call.respond(R.parameterError("accountId"))
                    return@put
                }
                val password = runCatching { call.receive<UpdatePassword>() }.getOrNull() ?: UpdatePassword("","")
                if (password.oldPassword == password.newPassword){
                    call.respond(R.parameterError("oldPassword","newPassword"))
                    return@put
                }
                when(val result = accountService.updatePassword(accountId.toInt(),password.oldPassword,password.newPassword)){
                    is RequestResult.Success<*> -> {
                        call.respond(R.success())
                    }
                    is RequestResult.Failure -> {
                        call.respond(R.error(result.errorMessage))
                    }
                }
            }
            get("/{accountId}"){
                val accountId = call.pathParameters["accountId"] ?: ""
                val regex = Regex("\\d*")
                if (!regex.matches(accountId)){
                    call.respond(R.parameterError("accountId"))
                    return@get
                }
                when(val result = accountService.getAccountById(accountId.toInt())){
                    is RequestResult.Success -> {
                        call.respond(R.success(data = result.data))
                    }
                    is RequestResult.Failure -> {
                        call.respond(result.errorMessage.toR())
                    }
                }
            }
        }
    }

}