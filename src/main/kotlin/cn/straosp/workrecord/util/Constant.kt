package cn.straosp.workrecord.util

object Constant {

    const val TOKEN_CLAIM_PHONE_KEY = "token_claim_phone"
    const val TOKEN_CLAIM_PASSWORD_KEY = "token_claim_password"
    const val TOKEN_CLAIM_ACCOUNT_ID_KEY = "token_claim_account_id"


    const val RESPONSE_SUCCESS_CODE = 200
    const val RESPONSE_SUCCESS_MESSAGE = "Success"

    const val RESPONSE_ERROR_PARAMETER_CODE = 201
    const val RESPONSE_ERROR_PARAMETER_MESSAGE = "参数异常"

    const val RESPONSE_SUCCESS_SELECT = "查询成功"

    const val REGISTER_PHONE_USED_CODE = 101
    const val REGISTER_PHONE_USED_MESSAGE = "手机号已注册"

    const val AUTHENTICATION_FAILED_CODE = 401
    const val AUTHENTICATION_FAILED_MESSAGE = "用户认证失败，请重新登录"

    const val CREATE_JOTTING_FAILED_CODE = 150
    const val CREATE_JOTTING_FAILED_MESSAGE = "保存便签失败"

    const val UPDATE_ACCOUNT_HEADER_FAILED_CODE = 170
    const val UPDATE_ACCOUNT_HEADER_FAILED_MESSAGE = "更新头像失败"

    const val DELETE_FILED_CODE = 180
    const val DELETE_FAILED_MESSAGE = "删除失败"

    const val ACTION_FAILED_CODE = 300
    const val ACTION_FAILED_MESSAGE = "操作失败"

    const val NOT_FOUND_ACCOUNT_CODE = 102
    const val NOT_FOUND_ACCOUNT_MESSAGE = "用户未找到"

    const val UPDATE_WORK_RECORD_FAILED_CODE = 190
    const val UPDATE_WORK_RECORD_FAILED_MESSAGE = "更新失败，更新后的时间内已经存在记录"



}