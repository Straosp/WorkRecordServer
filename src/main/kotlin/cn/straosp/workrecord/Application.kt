package cn.straosp.workrecord

import cn.straosp.workrecord.module.configureKoin
import cn.straosp.workrecord.plugin.*
import cn.straosp.workrecord.util.SaveFileUtil
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    SaveFileUtil.initEnvironment(environment = this.environment)
    configureKoin()
    configureAdministration()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
