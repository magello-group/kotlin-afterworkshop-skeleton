import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get

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
        val a: Nothing = TODO("IMPLEMENT ME")
)

// Just run me in intellij, or in terminal.
suspend fun main() {

    //TODO("Post an object instead of getting a string.")
    println("Going to get the website of google.")
    val a = webclient.get<String>("http://google.com")
    println("Google answered: $a")

}