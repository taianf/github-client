package br.com.feitosa.taian.github.client.routes

import br.com.feitosa.taian.github.client.constants.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.html.*

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
            call.respondHtml {
                body {
                    // Create a form that POSTs back to this same route
                    form(method = FormMethod.post) {
                        // handle any possible errors
                        val queryParams = call.request.queryParameters
                        val errorMsg = when {
                            "invalid" in queryParams -> "Sorry, incorrect username or password."
                            "no" in queryParams -> "Sorry, you need to be logged in to do that."
                            else -> null
                        }
                        if (errorMsg != null) {
                            div {
                                style = "color:red;"
                                +errorMsg
                            }
                        }
                        textInput(name = FormFields.USERNAME) {
                            placeholder = "user (${TestCredentials.USERNAME})"
                        }
                        br
                        passwordInput(name = FormFields.PASSWORD) {
                            placeholder = "password (${TestCredentials.PASSWORD})"
                        }
                        br
                        submitInput {
                            value = "Log in"
                        }
                    }
                }
            }
        }
        authenticate(AuthName.FORM) {
            post {
                // Get the principle (which we know we'll have)
                val principal = call.principal<UserIdPrincipal>() ?: return@post
                // Set the cookie
                call.sessions.set(principal)
                call.respondRedirect(CommonRoutes.PROFILE)
            }
        }
    }
}

internal fun Routing.logoutRoute() {
    get(CommonRoutes.LOGOUT) {
        // Purge ExamplePrinciple from cookie data
        call.sessions.clear<UserIdPrincipal>()
        call.respondRedirect(CommonRoutes.LOGIN)
    }
}

internal fun Route.profileRoute() {
    authenticate(AuthName.SESSION) {
        get(CommonRoutes.PROFILE) {
            val principal = call.principal<UserIdPrincipal>() ?: return@get
            call.respondHtml {
                body {
                    div {
                        +"Hello, ${principal}!"
                    }
                    div {
                        a(href = CommonRoutes.LOGOUT) {
                            +"Log out"
                        }
                    }
                }
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