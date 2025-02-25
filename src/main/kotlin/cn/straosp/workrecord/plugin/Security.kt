package cn.straosp.workrecord.plugin

import cn.straosp.workrecord.service.AccountService
import cn.straosp.workrecord.util.Constant
import cn.straosp.workrecord.util.R
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val accountService by inject<AccountService>()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                val phone = credential.payload.getClaim("phone").asString()
                val password = credential.payload.getClaim("password").asString()
                if (accountService.verifyAccount(phone,password).isSuccess){
                    JWTPrincipal(credential.payload)
                }
            }
            challenge { _, _ ->
                call.respond(R.error(Constant.AUTHENTICATION_FAILED_CODE, Constant.AUTHENTICATION_FAILED_MESSAGE))
            }
        }
    }
}
