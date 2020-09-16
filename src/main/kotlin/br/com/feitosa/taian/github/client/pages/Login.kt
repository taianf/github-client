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
            button {
                id = "sign-in"
                hidden = true
                +"Sign In with github"
            }
        }
    }
}
