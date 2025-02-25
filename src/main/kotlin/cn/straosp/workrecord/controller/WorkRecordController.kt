package cn.straosp.workrecord.controller

import cn.straosp.workrecord.bean.AddWorkRecord
import cn.straosp.workrecord.util.R
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.workRecordController(){
    route("/workRecord"){
        authenticate {
            post("/addWorkRecord") {
                val account = runCatching { call.principal<JWTPrincipal>() }.getOrNull()
                val workRecord = runCatching { call.receive<AddWorkRecord>() }.getOrNull() ?: call.respond(R.parameterError());return@post


            }
        }
    }
}