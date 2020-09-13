package br.com.feitosa.taian.github.client.pages

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.html.*
import io.ktor.util.pipeline.*
import kotlinx.html.*

internal suspend fun PipelineContext<Unit, ApplicationCall>.getProfilePage(principal: UserIdPrincipal) {
    call.respondHtml {
        head {
            title = "Github client - User Settings"
            getDefaultHead()
        }
        body {
            div {
                +"Hello, ${principal}!"
            }
            button {
                id = "sign-out"
                +"Sign Out"
            }
        }
    }
}