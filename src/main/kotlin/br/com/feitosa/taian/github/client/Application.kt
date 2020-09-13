@file:Suppress("UNUSED_PARAMETER", "unused")

package br.com.feitosa.taian.github.client

import br.com.feitosa.taian.github.client.constants.*
import br.com.feitosa.taian.github.client.routes.*
import com.google.auth.oauth2.*
import com.google.firebase.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import kotlin.collections.set

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
        configureFirebase()
    }
    // Enable cookies
    install(Sessions) {
        configureAuthCookie()
    }
    routing {
        static("/static") {
            resources("static")
        }
        homepageRoute()
        loginRoute()
        logoutRoute()
        profileRoute()
        notFoundRoute()
    }
}

fun configureFirebase() {
    val databaseUrl = System.getenv("FIREBASE_DATABASE") ?: "https://github-client-289022.firebaseio.com/"
    val options = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .setDatabaseUrl(databaseUrl)
        .build()
    var hasBeenInitialized = false
    val apps = FirebaseApp.getApps()
    for (app in apps) {
        if (app.name == FirebaseApp.DEFAULT_APP_NAME) {
            hasBeenInitialized = true
        }
    }
    if (!hasBeenInitialized) {
        FirebaseApp.initializeApp(options)
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
