package cn.straosp.workrecord.util

sealed class RequestResult{
    data class Failure(val errorMessage: RequestErrorMessage): RequestResult()
    data class Success<T>(val data: T): RequestResult()
}