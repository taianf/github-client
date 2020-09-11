package br.com.feitosa.taian.github.client.database

import br.com.feitosa.taian.github.client.constants.*
import com.google.cloud.datastore.*
import io.ktor.auth.*

fun checkUser(cred: UserPasswordCredential): UserIdPrincipal? {
    return if ((cred.name == TestCredentials.USERNAME && cred.password == TestCredentials.PASSWORD)) {
        UserIdPrincipal(cred.name)
    } else {
        null
    }
}

object QuickstartSample {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // Instantiates a client
        val datastore = DatastoreOptions.getDefaultInstance().service

        // The kind for the new entity
        val kind = "Task"
        // The name/ID for the new entity
        val name = "sampletask1"
        // The Cloud Datastore key for the new entity
        val taskKey = datastore.newKeyFactory().setKind(kind).newKey(name)

        // Prepares the new entity
        val task = Entity.newBuilder(taskKey)
            .set("description", "Buy milk")
            .build()

        // Saves the entity
        datastore.put(task)
        System.out.printf("Saved %s: %s%n", task.key.name, task.getString("description"))

        //Retrieve entity
        val retrieved = datastore[taskKey]
        System.out.printf("Retrieved %s: %s%n", taskKey.name, retrieved.getString("description"))
    }
}
