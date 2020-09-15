package br.com.feitosa.taian.github.client.routes

import br.com.feitosa.taian.github.client.constants.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

internal fun Routing.logoutRoute() {
    get(CommonRoutes.LOGOUT) {
        call.sessions.clear<UserIdPrincipal>()
        call.response.cookies.appendExpired("gitToken")
        call.response.cookies.appendExpired("firebaseToken")
        call.respondRedirect(CommonRoutes.LOGIN)
    }
}
