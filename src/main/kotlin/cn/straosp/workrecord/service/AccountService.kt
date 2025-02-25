package cn.straosp.workrecord.service

import cn.straosp.workrecord.bean.AccountInformation
import cn.straosp.workrecord.db.Account
import cn.straosp.workrecord.util.RequestResult

interface AccountService {

    fun verifyAccount(phone: String,password: String): Result<Account>
    fun registerAccount(phone: String,password: String): Result<Account>
    fun updateAccountInformation(accountId:Int,information: AccountInformation): Result<Boolean>
    fun uploadHeader(accountId: Int,headerFileName:String): RequestResult
    fun updatePassword(accountId: Int,oldPassword:String,newPassword: String): RequestResult
}