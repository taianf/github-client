package br.com.feitosa.taian.github.client.database

import com.google.firebase.auth.*

internal fun getAuthenticatedToken(idToken: String): FirebaseToken? {
    return try {
        FirebaseAuth.getInstance().verifyIdToken(idToken)
    } catch (ex: Exception) {
        null
    }
}