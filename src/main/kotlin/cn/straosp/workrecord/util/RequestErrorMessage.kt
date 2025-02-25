package cn.straosp.workrecord.util

import kotlinx.serialization.Serializable

@Serializable
data class RequestErrorMessage(
    val code:Int,val msg:String
): Throwable()

fun RequestErrorMessage.toR() = R.error(
    this.code,this.msg
)