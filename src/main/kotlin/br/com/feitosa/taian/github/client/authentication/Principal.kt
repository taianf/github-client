package br.com.feitosa.taian.github.client.authentication

import br.com.feitosa.taian.github.client.database.*
import io.ktor.auth.*

data class AppPrincipal(val name: String, val uid: String, val githubToken: String) : Principal

internal fun getAppPrincipal(firebaseToken: String, githubToken: String?): AppPrincipal? {
    val authenticatedToken = getAuthenticatedToken(firebaseToken)
    return if (authenticatedToken != null && githubToken != null) {
        AppPrincipal(authenticatedToken.email, authenticatedToken.uid, githubToken)
    } else {
        null
    }
}