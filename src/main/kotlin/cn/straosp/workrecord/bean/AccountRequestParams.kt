package cn.straosp.workrecord.bean

import kotlinx.serialization.Serializable

@Serializable
data class Login(val phone: String, val password: String)

@Serializable
data class AccountInformation(
    val username: String? = null,
    val email: String? = null,
    val address: String? = null,
    val header: String? = null
)

@Serializable
data class UpdatePassword(
    val oldPassword:String,
    val newPassword:String
)