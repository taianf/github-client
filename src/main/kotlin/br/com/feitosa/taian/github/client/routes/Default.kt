package br.com.feitosa.taian.github.client.routes

import br.com.feitosa.taian.github.client.constants.*
import br.com.feitosa.taian.github.client.pages.*
import com.google.firebase.auth.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

internal fun Routing.homepageRoute() {
    authenticate(AuthName.SESSION, optional = true) {
        get("/") {
            // Redirect user to login if they're not already logged in.
            // Otherwise redirect them to a page that requires auth.
            if (call.principal<UserIdPrincipal>() == null) {
                call.respondRedirect(CommonRoutes.LOGIN)
            } else {
                call.respondRedirect(CommonRoutes.PROFILE)
            }
        }
    }
}

internal fun Routing.loginRoute() {
    route(CommonRoutes.LOGIN) {
        get {
            val idToken = call.request.cookies["token"]
            if (idToken.isNullOrBlank()) {
                getLoginPage()
            } else {
                val firebaseToken = authenticateToken(idToken)
                if (firebaseToken == null) {
                    getLoginPage()
                } else {
                    val name = firebaseToken.name ?: "<user-without-name>"
                    val principal = UserIdPrincipal(name)
                    call.sessions.set(principal)
                    call.respondRedirect(CommonRoutes.PROFILE)
                }
            }
        }
    }
}

fun authenticateToken(idToken: String): FirebaseToken? {
    return try {
        FirebaseAuth.getInstance().verifyIdToken(idToken)
    } catch (ex: Exception) {
        null
    }
}

internal fun Routing.logoutRoute() {
    get(CommonRoutes.LOGOUT) {
        call.sessions.clear<UserIdPrincipal>()
        call.response.cookies.append("token", "")
        call.respondRedirect(CommonRoutes.LOGIN)
    }
}

internal fun Route.profileRoute() {
    authenticate(AuthName.SESSION) {
        get(CommonRoutes.PROFILE) {
            val principal = call.principal<UserIdPrincipal>() ?: return@get
            getProfilePage(principal)
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