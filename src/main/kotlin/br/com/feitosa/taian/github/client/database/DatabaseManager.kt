package br.com.feitosa.taian.github.client.database

import br.com.feitosa.taian.github.client.authentication.*
import com.github.kittinunf.fuel.*
import com.github.kittinunf.result.*
import com.google.cloud.datastore.*
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
    val userName: String = "",
    val email: String = "",
    val repositories: List<Repository> = mutableListOf(),
) {
    constructor(appPrincipal: AppPrincipal) : this(
        appPrincipal.userName,
        appPrincipal.email,
    )
}

object DatabaseManager {
    val instance: Datastore = DatastoreOptions.getDefaultInstance().service
}

fun getGithubStarredRepos(userName: String): List<Repository> {
    val url = "https://api.github.com/users/${userName}/starred"
    val (_, _, result) = url.httpGet().response()
    when (result) {
        is Result.Failure -> {
            val ex = result.getException()
            println(ex)
        }
        is Result.Success -> {
            val data = Json.parseToJsonElement(String(result.get()))
            return data.jsonArray.map {
                Repository(it.jsonObject["html_url"].toString(), it.jsonObject["name"].toString())
            }
        }
    }
    return mutableListOf()
}

internal fun createUserData(principal: AppPrincipal, repositories: List<Repository>) {
    val uid = principal.uid
    val userKey: Key = DatabaseManager.instance.newKeyFactory().setKind("userProfile").newKey(uid)
    val userData = Entity.newBuilder(userKey)
        .set("userName", principal.userName)
        .set("email", principal.email)
        .set("repositories", Json.encodeToString(repositories))
        .build()
    DatabaseManager.instance.put(userData)
}

internal fun readUserData(principal: AppPrincipal): AppProfile {
    val uid = principal.uid
    val userKey: Key = DatabaseManager.instance.newKeyFactory().setKind("userProfile").newKey(uid)
    val userData = DatabaseManager.instance.get(userKey)
    val repositoriesFromGithub: List<Repository> = getGithubStarredRepos(principal.userName)
    return if (userData == null) {
        createUserData(principal, repositoriesFromGithub)
        AppProfile(principal)
    } else {
        val repositoriesFromDatabase = Json.decodeFromString<List<Repository>>(userData.getString("repositories"))
        val repos: List<Repository> = repositoriesFromGithub.map { repoFromGithub ->
            val repository: Repository? = repositoriesFromDatabase.firstOrNull { repoFromDB ->
                repoFromDB.url == repoFromGithub.url
            }
            Repository(repoFromGithub.url, repoFromGithub.name, repository?.tags ?: mutableListOf())
        }
        AppProfile(
            userData.getString("userName"),
            userData.getString("email"),
            repos,
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
