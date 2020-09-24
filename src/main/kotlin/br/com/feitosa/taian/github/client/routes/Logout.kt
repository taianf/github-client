package br.com.feitosa.taian.github.client.routes

import br.com.feitosa.taian.github.client.authentication.*
import br.com.feitosa.taian.github.client.constants.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

internal fun Routing.logoutRoute() {
    get(CommonRoutes.LOGOUT) {
        call.sessions.clear<AppPrincipal>()
        call.response.cookies.appendExpired("githubUser")
        call.response.cookies.appendExpired("firebaseToken")
        call.respondRedirect(CommonRoutes.LOGIN)
    }
}
