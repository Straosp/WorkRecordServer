package cn.straosp.workrecord.service

import cn.straosp.workrecord.bean.AccountInformation
import cn.straosp.workrecord.db.Account
import cn.straosp.workrecord.util.RequestResult

interface AccountService {

    fun verifyAccount(phone: String,password: String): RequestResult<Account>
    fun registerAccount(phone: String,password: String): RequestResult<Account>
    fun updateAccountInformation(accountId:Int,information: AccountInformation): RequestResult<Boolean>
    fun uploadHeader(accountId: Int,headerFileName:String): RequestResult<Boolean>
    fun updatePassword(accountId: Int,oldPassword:String,newPassword: String): RequestResult<Boolean>
    fun getAccountById(accountId: Int):RequestResult<Account>
}