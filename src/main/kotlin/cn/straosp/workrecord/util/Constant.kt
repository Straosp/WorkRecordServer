package cn.straosp.workrecord.util

object Constant {

    const val TOKEN_CLAIM_PHONE_KEY = "token_claim_phone"
    const val TOKEN_CLAIM_PASSWORD_KEY = "token_claim_password"
    const val TOKEN_CLAIM_ACCOUNT_ID_KEY = "token_claim_account_id"

    const val RESPONSE_SUCCESS_CODE = 200
    const val RESPONSE_SUCCESS_MESSAGE = "Success"

    private const val ACCOUNT_ERROR_PREFIX = 300

    const val ACCOUNT_LOGIN_FAILED_CODE = ACCOUNT_ERROR_PREFIX + 1
    const val ACCOUNT_LOGIN_FAILED_MESSAGE = "账号或密码错误"

    const val REGISTER_PHONE_USED_CODE = ACCOUNT_ERROR_PREFIX + 2
    const val REGISTER_PHONE_USED_MESSAGE = "手机号已注册"

    const val AUTHENTICATION_FAILED_CODE = ACCOUNT_ERROR_PREFIX + 3
    const val AUTHENTICATION_FAILED_MESSAGE = "用户认证失败，请重新登录"

    const val UPDATE_ACCOUNT_HEADER_FAILED_CODE = ACCOUNT_ERROR_PREFIX + 4
    const val UPDATE_ACCOUNT_HEADER_FAILED_MESSAGE = "更新头像失败"

    const val ACCOUNT_SELECT_FAILED_CODE = ACCOUNT_ERROR_PREFIX + 5
    const val ACCOUNT_SELECT_FAILED_MESSAGE = "查询用户失败"


    private const val WORK_RECORD_ERROR_PREFIX = 400

    const val WORK_RECORD_DELETE_FILED_CODE = WORK_RECORD_ERROR_PREFIX  + 1
    const val WORK_RECORD_DELETE_FILED_MESSAGE = "工作记录删除失败"

    const val UPDATE_WORK_RECORD_FAILED_CODE = WORK_RECORD_ERROR_PREFIX  + 2
    const val UPDATE_WORK_RECORD_FAILED_MESSAGE = "更新失败，更新后的时间内已经存在记录"

    const val INSERT_WORK_RECORD_FAILED_CODE = WORK_RECORD_ERROR_PREFIX  + 3
    const val INSERT_WORK_RECORD_FAILED_MESSAGE = "添加失败，当前时间已经存在记录"

    private const val OTHER_ERROR_PREFIX = 200

    const val RESPONSE_ERROR_PARAMETER_CODE = OTHER_ERROR_PREFIX + 1
    const val RESPONSE_ERROR_PARAMETER_MESSAGE = "参数异常: "






}