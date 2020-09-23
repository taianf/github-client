package br.com.feitosa.taian.github.client.database

import br.com.feitosa.taian.github.client.authentication.*
import com.google.cloud.datastore.*
import com.google.firebase.auth.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*


@Serializable
data class Repository(
    val url: String,
    val name: String = "",
    val tags: List<String> = mutableListOf(),
)

@Serializable
data class AppProfile(
    val name: String = "",
    val email: String = "",
    val repositories: List<Repository> = mutableListOf(),
) {
    constructor(appPrincipal: AppPrincipal) : this(
        appPrincipal.name,
        appPrincipal.email,
    )
}

object DatabaseManager {
    val instance: Datastore = DatastoreOptions.getDefaultInstance().service
}

internal fun getAuthenticatedToken(idToken: String): FirebaseToken? {
    return try {
        FirebaseAuth.getInstance().verifyIdToken(idToken)
    } catch (ex: Exception) {
        null
    }
}

internal fun createUserData(principal: AppPrincipal) {
    val uid = principal.uid
    val appProfile = AppProfile(
        principal.name,
        principal.email,
    )
    val userKey: Key = DatabaseManager.instance.newKeyFactory().setKind("userProfile").newKey(uid)
    val userData = Entity.newBuilder(userKey)
        .set("name", appProfile.name)
        .set("email", appProfile.email)
        .set("repositories", Json.encodeToString(appProfile.repositories))
        .build()
    DatabaseManager.instance.put(userData)
}

internal fun readUserData(principal: AppPrincipal): AppProfile {
    val uid = principal.uid
    val userKey: Key = DatabaseManager.instance.newKeyFactory().setKind("userProfile").newKey(uid)
    val userData = DatabaseManager.instance.get(userKey)
    return if (userData == null) {
        createUserData(principal)
        AppProfile(principal)
    } else {
        AppProfile(
            userData.getString("name"),
            userData.getString("email"),
            Json.decodeFromString(userData.getString("repositories")),
        )
    }
}
//
//internal fun updateUserData(principal: AppPrincipal): Any {
//    val uid = principal.uid
//    val ref = FirebaseDatabase.getInstance().getReference("user/profile/$uid")
//}
//
//
//internal fun deleteUserData(principal: AppPrincipal): Any {
//    val uid = principal.uid
//    val ref = FirebaseDatabase.getInstance().getReference("user/profile/$uid")
//}
//
