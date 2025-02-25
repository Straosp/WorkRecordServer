package cn.straosp.workrecord

import cn.straosp.workrecord.module.configureKoin
import cn.straosp.workrecord.plugin.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureKoin()
    configureAdministration()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
