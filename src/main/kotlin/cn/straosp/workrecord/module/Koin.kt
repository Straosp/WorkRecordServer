package cn.straosp.workrecord.module

import cn.straosp.workrecord.service.AccountService
import cn.straosp.workrecord.service.WorkRecordService
import cn.straosp.workrecord.service.impl.AccountServiceImpl
import cn.straosp.workrecord.service.impl.WorkRecordServiceImpl
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val serviceModule = module {
    single<AccountService>{  AccountServiceImpl() }
    single<WorkRecordService>{  WorkRecordServiceImpl() }
}

fun Application.configureKoin(){
    install(Koin) {
        modules(
            serviceModule
        )
    }
}