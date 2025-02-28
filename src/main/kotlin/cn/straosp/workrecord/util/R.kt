package cn.straosp.workrecord.util

import kotlinx.serialization.Serializable

@Serializable
data class R<T>(
    val code:Int,
    val message:String?,
    val data:T?
) {

    companion object {

        fun success() = R(Constant.RESPONSE_SUCCESS_CODE,Constant.RESPONSE_SUCCESS_MESSAGE,null)
        fun <T> success(data:T) = R(200,"success",data)
        fun success(message:String) = R(200,message,null)
        fun <T> success(message:String,data:T) = R(200,message,data)
        fun success(map: Map<String,Any>) = R(200,"success",map)
        fun <T> selectSuccess(data:T) = R(Constant.RESPONSE_SUCCESS_CODE,Constant.RESPONSE_SUCCESS_SELECT,data)

        fun error() = R(100,"request error",null)
        fun error(message: String) = R(101,message,null)
        fun <T> error(message: String,data:T) = R(102,message,data)
        fun error(code: Int,message: String) = R(code,message,null)
        fun error(errorMessage: RequestErrorMessage) = R(errorMessage.code,errorMessage.msg ?: "",null)
        fun parameterError() = R(Constant.RESPONSE_ERROR_PARAMETER_CODE,Constant.RESPONSE_ERROR_PARAMETER_MESSAGE,null)

    }

}