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
    val name: String,
    val tags: Set<String> = mutableSetOf(),
)

@Serializable
data class AppProfile(
    val uid: String,
    val userName: String,
    val email: String,
    val repositories: Set<Repository> = mutableSetOf(),
) {
    constructor(uid: String, appPrincipal: AppPrincipal) : this(
        uid,
        appPrincipal.userName,
        appPrincipal.email,
    )
}

object DatabaseManager {
    val instance: Datastore = DatastoreOptions.getDefaultInstance().service
}

internal fun getGithubStarredRepos(userName: String): Set<Repository> {
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
            }.toSet()
        }
    }
    return mutableSetOf()
}

internal fun createUserData(principal: AppPrincipal, repositories: Set<Repository>) {
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
    val userData = DatabaseManager.instance.get(getUserKey(uid))
    val repositoriesFromGithub: Set<Repository> = getGithubStarredRepos(principal.userName)
    return if (userData == null) {
        createUserData(principal, repositoriesFromGithub)
        AppProfile(uid, principal)
    } else {
        val repositoriesFromDatabase = Json.decodeFromString<List<Repository>>(userData.getString("repositories"))
        val repos: Set<Repository> = repositoriesFromGithub.map { repoFromGithub ->
            val repository: Repository? = repositoriesFromDatabase.firstOrNull { repoFromDB ->
                repoFromDB.url == repoFromGithub.url
            }
            Repository(repoFromGithub.url, repoFromGithub.name, repository?.tags ?: mutableSetOf())
        }.toSet()
        getAppProfileFromUserData(uid, userData, repos)
    }
}

private fun getUserKey(uid: String): Key {
    return DatabaseManager.instance.newKeyFactory().setKind("userProfile").newKey(uid)
}

internal fun insertTag(appProfile: AppProfile) {
    val uid = appProfile.uid
    val userKey = getUserKey(uid)
    val userData = DatabaseManager.instance.get(userKey)
    val appProfileFromUserData = getAppProfileFromUserData(uid, userData)
    val newRepos: Set<Repository> = appProfile.repositories.map { repository ->
        val oldTags: Set<String> =
            appProfileFromUserData.repositories.firstOrNull { it.url == repository.url }?.tags ?: mutableSetOf()
        val mergedTags = repository.tags + oldTags
        Repository(repository.url, repository.name, mergedTags)
    }.toSet()
    val newUserData = Entity.newBuilder(userKey)
        .set("userName", appProfile.userName)
        .set("email", appProfile.email)
        .set("repositories", Json.encodeToString(newRepos))
        .build()
    DatabaseManager.instance.put(newUserData)
}

//internal fun removeTag(appProfile: AppProfile): Any {
//}

private fun getAppProfileFromUserData(
    uid: String,
    userData: Entity,
    repos: Set<Repository> = mutableSetOf(),
): AppProfile {
    return AppProfile(
        uid,
        userData.getString("userName"),
        userData.getString("email"),
        if (repos.isNotEmpty()) repos else Json.decodeFromString(userData.getString("repositories")),
    )
}