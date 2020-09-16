package br.com.feitosa.taian.github.client.routes

import br.com.feitosa.taian.github.client.authentication.*
import br.com.feitosa.taian.github.client.constants.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

internal fun Routing.homepageRoute() {
    authenticate(AuthName.SESSION, optional = true) {
        get("/") {
            // Redirect user to login if they're not already logged in.
            // Otherwise redirect them to a page that requires auth.
            if (call.principal<AppPrincipal>() == null) {
                call.respondRedirect(CommonRoutes.LOGIN)
            } else {
                call.respondRedirect(CommonRoutes.PROFILE)
            }
        }
    }
}

internal fun Route.notFoundRoute() {
    get("/*") {
        call.respond(
            HttpStatusCode.NotFound,
            TextContent("Page not found", ContentType.Text.Plain.withCharset(Charsets.UTF_8))
        )
    }
}