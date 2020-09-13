package br.com.feitosa.taian.github.client.pages

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.util.pipeline.*
import kotlinx.html.*

internal suspend fun PipelineContext<Unit, ApplicationCall>.getLoginPage() {
    call.respondHtml {
        head {
            title = "Github client"
            getDefaultHead()
        }
        body {
            h1 { +"Github client app" }
            div { id = "firebaseui-auth-container" }
            button {
                id = "sign-out"
                hidden = true
                +"Sign Out"
            }
        }
    }
}
