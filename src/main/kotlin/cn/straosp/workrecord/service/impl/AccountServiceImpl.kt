package cn.straosp.workrecord.service.impl

import cn.straosp.workrecord.bean.AccountInformation
import cn.straosp.workrecord.db.*
import cn.straosp.workrecord.service.AccountService
import cn.straosp.workrecord.util.Constant
import cn.straosp.workrecord.util.RequestErrorMessage
import cn.straosp.workrecord.util.RequestResult
import org.ktorm.dsl.*
import org.ktorm.entity.*

class AccountServiceImpl: AccountService {

    override fun getAccountById(accountId: Int): RequestResult<Account> {
        val account = AppDatabase.database.sequenceOf(AccountTable).find { it.id eq accountId }?.toAccount()
            ?: return RequestResult.Failure(RequestErrorMessage(Constant.ACCOUNT_SELECT_FAILED_CODE,Constant.ACCOUNT_SELECT_FAILED_MESSAGE))
        return RequestResult.Success(account)
    }

    override fun verifyAccount(phone: String, password: String): RequestResult<Account> {
        val account = AppDatabase.database.sequenceOf(AccountTable).find { it.phone eq phone and (it.password eq password) }?.toAccount()
            ?: return RequestResult.Failure(RequestErrorMessage(Constant.AUTHENTICATION_FAILED_CODE,Constant.AUTHENTICATION_FAILED_MESSAGE))
        return RequestResult.Success(account)
    }

    override fun registerAccount(phone: String, password: String): RequestResult<Account> {
        val account = AppDatabase.database.sequenceOf(AccountTable).find { it.phone eq phone }?.toAccount()
        if (null != account){
            return RequestResult.Failure(RequestErrorMessage(Constant.REGISTER_PHONE_USED_CODE,Constant.REGISTER_PHONE_USED_MESSAGE))
        }
        val index = AppDatabase.database.insertAndGenerateKey(AccountTable){
            set(AccountTable.phone,phone)
            set(AccountTable.password,password)
            set(AccountTable.username,phone)
        }
        val registerAccount = AppDatabase.database.sequenceOf(AccountTable).find { it.id eq (index as Int) }?.toAccount()
            ?: return RequestResult.Failure(RequestErrorMessage(Constant.ACCOUNT_SELECT_FAILED_CODE,Constant.ACCOUNT_SELECT_FAILED_MESSAGE))
        return RequestResult.Success(registerAccount)
    }

    override fun updateAccountInformation(accountId:Int,information: AccountInformation): RequestResult<Boolean> {
        val result = AppDatabase.database.update(AccountTable){
            if (!information.username.isNullOrEmpty()){
                set(AccountTable.username,information.username)
            }
            if (!information.email.isNullOrEmpty()){
                set(AccountTable.email,information.email)
            }
            if (!information.address.isNullOrEmpty()){
                set(AccountTable.address,information.address)
            }
            if (!information.header.isNullOrEmpty()){
                set(AccountTable.header,information.header)
            }
            where {
                AccountTable.id eq accountId
            }
        }
        return if (result == 1) RequestResult.Success(true) else RequestResult.Failure(RequestErrorMessage(
            Constant.REGISTER_PHONE_USED_CODE,Constant.REGISTER_PHONE_USED_MESSAGE
        ))
    }

    override fun uploadHeader(accountId: Int, headerFileName: String): RequestResult<Boolean> {
        val result = AppDatabase.database.update(AccountTable) {
            set(AccountTable.header,headerFileName)
            where {
                AccountTable.id eq accountId
            }
        }
        return if (result == 1) RequestResult.Success(true) else RequestResult.Failure(RequestErrorMessage(Constant.UPDATE_ACCOUNT_HEADER_FAILED_CODE,Constant.UPDATE_ACCOUNT_HEADER_FAILED_MESSAGE))
    }

    override fun updatePassword(accountId: Int, oldPassword: String, newPassword: String): RequestResult<Boolean> {
        val result = AppDatabase.database.update(AccountTable) {
            set(AccountTable.password,newPassword)
            where {
                (AccountTable.id eq accountId) and (AccountTable.password eq oldPassword)
            }
        }
        return if (result == 1) RequestResult.Success(true) else RequestResult.Failure(RequestErrorMessage(Constant.UPDATE_ACCOUNT_HEADER_FAILED_CODE,Constant.UPDATE_ACCOUNT_HEADER_FAILED_MESSAGE))
    }


}