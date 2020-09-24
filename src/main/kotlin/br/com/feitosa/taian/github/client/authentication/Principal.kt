package br.com.feitosa.taian.github.client.authentication

import com.google.firebase.auth.*
import io.ktor.auth.*

data class AppPrincipal(val userName: String, val email: String, val uid: String, val githubUser: String) : Principal

internal fun getAppPrincipal(firebaseToken: String, githubUser: String): AppPrincipal? {
    val authenticatedToken = getAuthenticatedToken(firebaseToken)
    return if (authenticatedToken != null) {
        AppPrincipal(githubUser, authenticatedToken.email, authenticatedToken.uid, githubUser)
    } else {
        null
    }
}

internal fun getAuthenticatedToken(idToken: String): FirebaseToken? {
    return try {
        FirebaseAuth.getInstance().verifyIdToken(idToken)
    } catch (ex: Exception) {
        null
    }
}
