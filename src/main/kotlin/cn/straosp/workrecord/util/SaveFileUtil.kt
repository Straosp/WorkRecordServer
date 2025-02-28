package cn.straosp.workrecord.util

import io.ktor.server.application.ApplicationEnvironment
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SaveFileUtil {

    companion object {

        private lateinit var HEADER_FILE_PATH: String

        fun initEnvironment(environment: ApplicationEnvironment){
            HEADER_FILE_PATH = environment.config.property("file").getString()
            println(HEADER_FILE_PATH)
            val headerFile = File(HEADER_FILE_PATH)
            if (!headerFile.exists()){
                headerFile.mkdirs()
            }
        }
        @Volatile
        private var instance: SaveFileUtil? = null

        fun getInstance(): SaveFileUtil{
            if (null == instance){
                synchronized(SaveFileUtil){
                    instance = SaveFileUtil()
                }
            }
            return instance!!
        }

    }

    suspend fun saveHeaderFile(fileName: String, provider: () -> ByteReadChannel){
        withContext(Dispatchers.IO) {
            val file = File(
                HEADER_FILE_PATH,
                fileName
            )
            file.createNewFile()
            file.outputStream().use { stream ->
                provider.invoke().copyTo(stream)
            }
        }
    }

    suspend fun getFile(fileName: String) = File(
        HEADER_FILE_PATH,
        fileName
    )



}