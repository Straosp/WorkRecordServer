package cn.straosp.workrecord.db

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

@Serializable
data class Account(
    val id:Int,
    val username:String,
    val header:String,
    val phone:String,
    val password:String,
    val email:String,
    val sex:Int,
    val address:String
)

interface AccountEntity: Entity<AccountEntity> {
    companion object: Entity.Factory<AccountEntity>()
    val id:Int
    val username:String
    val header:String
    val phone:String
    val password:String
    val email:String
    val sex:Int
    val address:String
}

object AccountTable: Table<AccountEntity>("account") {
    val id = int("id").primaryKey().bindTo { it.id }
    val username = varchar("username").bindTo { it.username }
    val header = varchar("header").bindTo { it.header }
    val phone = varchar("phone").bindTo { it.phone }
    val password = varchar("password").bindTo { it.password }
    val email = varchar("email").bindTo { it.email }
    val sex = int("sex").bindTo { it.sex }
    val address = varchar("address").bindTo { it.address }
}

fun AccountEntity.toAccount(): Account = Account(
    id = id,
    username = username,
    header = header,
    phone = phone,
    password = password,
    email = email,
    sex = sex,
    address = address
)