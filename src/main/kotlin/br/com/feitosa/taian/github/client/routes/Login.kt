package br.com.feitosa.taian.github.client.routes

import br.com.feitosa.taian.github.client.authentication.*
import br.com.feitosa.taian.github.client.constants.*
import br.com.feitosa.taian.github.client.pages.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

internal fun Routing.loginRoute() {
    route(CommonRoutes.LOGIN) {
        get {
            val firebaseToken = call.request.cookies["firebaseToken"]
            val githubUser = call.request.cookies["githubUser"]
            if (firebaseToken.isNullOrBlank() || githubUser.isNullOrBlank()) {
                getLoginPage()
            } else {
                val appPrincipal = getAppPrincipal(firebaseToken, githubUser)
                if (appPrincipal == null) {
                    getLoginPage()
                } else {
                    call.sessions.set(appPrincipal)
                    call.respondRedirect(CommonRoutes.PROFILE)
                }
            }
        }
    }
}