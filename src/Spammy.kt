package se.r2m.kotlin.aw

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

val webclient = HttpClient(Apache) {
    install(JsonFeature) {
        serializer = GsonSerializer() // Take object and do automatic serialisation
    }
}

// TODO: Convert to data class, easier to work with.
const val dataStringExample = """
{
    "uuid": "<INSERT_UNIQUE_ID_PER_CALL_HERE>", // Don't know why but need to send tis 
    "personName": "<WHO_TO_MODIFY>", // Who to get score
    "scoreType": "INC" // increment the score
}
"""

data class SendMe(
        val a: Nothing = TODO("Implement the SendMe data class.")
)


// Just run me in intellij, or in terminal.
// `suspend` is used to be able to run coroutine functions - such as the ktor webclient.
suspend fun main() {
    val list: MutableList<Deferred<Any>> = mutableListOf()
    repeat(5) {
        list.add(
            GlobalScope.async(Dispatchers.IO) {
                //TODO("Post an object instead of getting a string.")
                println("Going to get the website of google.")
                val a = webclient.get<String>("http://google.com")
                delay(1000)
                println("Google answered: $a")
            }
        )
    }
    // Let all run before we continue and exit.
    list.forEach {
        it.await()
    }
}