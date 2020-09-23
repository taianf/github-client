package br.com.feitosa.taian.github.client.pages

import br.com.feitosa.taian.github.client.authentication.*
import br.com.feitosa.taian.github.client.database.*
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.util.pipeline.*
import kotlinx.html.*

internal suspend fun PipelineContext<Unit, ApplicationCall>.getProfilePage(principal: AppPrincipal) {
    val userData: AppProfile = readUserData(principal)
    call.respondHtml {
        head {
            title = "Github client - User Settings"
            getDefaultHead()
        }
        body {
            div {
                +"Hello, ${userData.email}!"
            }
            div {
                p { +"Repositories:" }
                div {
                    userData.repositories.forEach {
                        p { +it.name }
                        ul {
                            it.tags.forEach {
                                li {
                                    +it
                                }
                            }
                        }
                    }
                }
            }
            button {
                id = "sign-out"
                +"Sign Out"
            }
        }
    }
}

