package cn.straosp.workrecord.plugin

import cn.straosp.workrecord.controller.accountController
import cn.straosp.workrecord.controller.workRecordController
import cn.straosp.workrecord.util.Constant
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*

fun RoutingContext.getAccountId(): Int{
    val account = runCatching { call.principal<JWTPrincipal>() }.getOrNull()
    return account?.payload?.getClaim(Constant.TOKEN_CLAIM_ACCOUNT_ID_KEY)?.asInt() ?: 0
}

fun Application.configureRouting() {
    routing {
        options {
            call.respond(HttpStatusCode.OK)
        }
        accountController()
        workRecordController()
    }
}
