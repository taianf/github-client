package br.com.feitosa.taian.github.client.routes

import br.com.feitosa.taian.github.client.authentication.*
import br.com.feitosa.taian.github.client.constants.*
import br.com.feitosa.taian.github.client.pages.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

internal fun Route.profileRoute() {
    authenticate(AuthName.SESSION) {
        get(CommonRoutes.PROFILE) {
            val principal = call.principal<AppPrincipal>() ?: return@get
            getProfilePage(principal)
        }
    }
}

