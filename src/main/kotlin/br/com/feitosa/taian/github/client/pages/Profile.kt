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
                +"Hello, ${userData.userName}!"
            }
            div {
                h1 { +"Repositories:" }
                div {
                    userData.repositories.forEach { repository ->
                        h4 { +repository.name }
                        input {
                            type = InputType.text
                            id = "input-$repository"
                            placeholder = "new tag..."
                        }
                        span {
                            button {
                                onClick = "addTag()"
                                id = "addBtn-$repository"
                                +"Add tag"
                            }
                        }
                        ul {
                            +"Tags:"
                            repository.tags.forEach { tag ->
                                li {
                                    +tag
                                }
                                button {
                                    onClick = "removeTag()"
                                    id = "removeBtn-$repository-$tag"
                                    +"Remove tag"
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

