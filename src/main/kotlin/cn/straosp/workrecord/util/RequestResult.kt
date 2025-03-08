package cn.straosp.workrecord.util

sealed class RequestResult<out T>{
    data class Failure(val errorMessage: RequestErrorMessage): RequestResult<Nothing>()
    data class Success<out T>(val data: T): RequestResult<T>()
}
fun <T> RequestResult<T>.isSuccess(): Boolean {
    return this is RequestResult.Success<T>
}