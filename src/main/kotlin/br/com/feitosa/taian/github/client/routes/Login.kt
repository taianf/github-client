package br.com.feitosa.taian.github.client.routes

import br.com.feitosa.taian.github.client.constants.*
import br.com.feitosa.taian.github.client.database.*
import br.com.feitosa.taian.github.client.pages.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

internal fun Routing.loginRoute() {
    route(CommonRoutes.LOGIN) {
        get {
            val firebaseToken = call.request.cookies["firebaseToken"]
            val githubToken = call.request.cookies["githubToken"]
            if (firebaseToken.isNullOrBlank()) {
                getLoginPage()
            } else {
                val authenticatedToken = getAuthenticatedToken(firebaseToken)
                if (authenticatedToken == null) {
                    getLoginPage()
                } else {
                    val name = authenticatedToken.name ?: authenticatedToken.email
                    val principal = UserIdPrincipal(name)
                    call.sessions.set(principal)
                    call.respondRedirect(CommonRoutes.PROFILE)
                }
            }
        }
    }
}

