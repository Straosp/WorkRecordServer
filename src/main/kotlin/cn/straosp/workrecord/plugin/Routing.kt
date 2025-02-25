package cn.straosp.workrecord.plugin

import cn.straosp.workrecord.controller.accountController
import cn.straosp.workrecord.controller.workRecordController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*

fun Application.configureRouting() {
    routing {
        accountController()
        workRecordController()
    }
}
