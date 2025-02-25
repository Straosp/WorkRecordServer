package cn.straosp.workrecord.db

import com.alibaba.druid.pool.DruidDataSourceFactory
import io.ktor.server.config.*
import org.ktorm.database.Database

object AppDatabase {

    private val application = ApplicationConfig("application.yaml")
    private var databaseURL: String = ""
    private var username: String = ""
    private var password: String = ""
    private var driverClassName: String = ""
    private var initialSize:String = "10"
    private var maxActive:String = "10"

    lateinit var database: Database

    init {
        databaseURL = application.propertyOrNull("database.url")?.getString() ?: ""
        username = application.propertyOrNull("database.username")?.getString() ?: ""
        password = application.propertyOrNull("database.password")?.getString() ?: ""
        driverClassName = application.propertyOrNull("database.driverClassName")?.getString() ?: ""
        initialSize = application.propertyOrNull("database.initialSize")?.getString() ?: "10"
        maxActive = application.propertyOrNull("database.maxActive")?.getString() ?: "10"
        val map = mapOf(
            "driverClassName" to driverClassName,
            "url" to databaseURL,
            "username" to username,
            "password" to password,
            "initialSize" to initialSize,
            "maxActive" to maxActive
        )
        database = Database.connect(DruidDataSourceFactory.createDataSource(map))
    }


}