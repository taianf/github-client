@file:Suppress("UNUSED_PARAMETER", "unused")

package br.com.feitosa.taian.github.client

import br.com.feitosa.taian.github.client.constants.*
import br.com.feitosa.taian.github.client.database.*
import br.com.feitosa.taian.github.client.routes.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        watchPaths = listOf("github-client"),
        port = 8080,
        module = Application::main
    ).apply { start(true) }
}

@kotlin.jvm.JvmOverloads
fun Application.main(testing: Boolean = false) {
    // This adds Date and Server headers to each response, and allows custom additional headers
    install(DefaultHeaders)
    // This uses use the logger to log every call (request/response)
    install(CallLogging)
    // Enabling partial content support, so people will be able to resume larger static files on connections with frequent problems
    install(PartialContent)
    // Enable user authentication
    install(Authentication) {
        configureSessionAuth()
        configureFormAuth()
    }
    // Enable cookies
    install(Sessions) {
        configureAuthCookie()
    }
    routing {
        homepageRoute()
        loginRoute()
        logoutRoute()
        profileRoute()
        notFoundRoute()
    }
}

private fun Sessions.Configuration.configureAuthCookie() {
    cookie<UserIdPrincipal>(
        // We set a cookie by this name upon login.
        Cookies.AUTH_COOKIE,
        // Stores session contents in memory...good for development only.
        storage = SessionStorageMemory()
    ) {
        cookie.path = "/"
        // CSRF protection in modern browsers. Make sure your important side-effect-y operations, like ordering,
        // uploads, and changing settings, use "unsafe" HTTP verbs like POST and PUT, not GET or HEAD.
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies#SameSite_cookies
        cookie.extensions["SameSite"] = "lax"
    }
}

private fun Authentication.Configuration.configureFormAuth() {
    form(AuthName.FORM) {
        userParamName = FormFields.USERNAME
        passwordParamName = FormFields.PASSWORD
        challenge {
            // I don't think form auth supports multiple errors, but we're conservatively assuming there will be at
            // most one error, which we handle here. Worst case, we just send the user to login with no context.
            val errors: List<AuthenticationFailedCause> = call.authentication.allFailures
            when (errors.singleOrNull()) {
                AuthenticationFailedCause.InvalidCredentials ->
                    call.respondRedirect("${CommonRoutes.LOGIN}?invalid")

                AuthenticationFailedCause.NoCredentials ->
                    call.respondRedirect("${CommonRoutes.LOGIN}?no")

                else ->
                    call.respondRedirect(CommonRoutes.LOGIN)
            }
        }
        validate { cred: UserPasswordCredential ->
            // Realistically you'd look up the user in a database or something here; this is just a toy example.
            // The values here will be whatever was submitted in the form.
            checkUser(cred)
        }
    }
}


private fun Authentication.Configuration.configureSessionAuth() {
    session<UserIdPrincipal>(AuthName.SESSION) {
        challenge {
            // What to do if the user isn't authenticated
            call.respondRedirect("${CommonRoutes.LOGIN}?no")
        }
        validate { session: UserIdPrincipal ->
            // If you need to do additional validation on session data, you can do so here.
            session
        }
    }
}
