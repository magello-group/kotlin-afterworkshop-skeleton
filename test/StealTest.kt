import com.google.gson.Gson
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.atomicfu.atomic
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

val gson = Gson()

class StealTest {

    @Before fun cleanup(){
        highscores.clear()
    }

    @Test
    fun `test error when media type is not set`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/steal").apply {
                // Cannot run without media type
                assertEquals(expected = HttpStatusCode.UnsupportedMediaType, actual = response.status())
            }
        }
    }

    @Test
    fun `test error when data is not serialiseable`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/steal") {
                addHeader("content-type", ContentType.Application.Json.toString())
            }.apply {
                // Cannot run without media type
                assertEquals(expected = HttpStatusCode.BadRequest, actual = response.status())
            }
        }
    }

    @Test
    fun `test error when data when missing from`() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/steal") {
                addHeader("content-type", ContentType.Application.Json.toString())
                setBody(
                    gson.toJson(
                        StealBody("Kalle", "Olle")
                    )
                )
            }.apply {
                // Cannot run without media type
                assertEquals(expected = HttpStatusCode.NotAcceptable, actual = response.status())
                assertEquals(expected = "Cannot steal from Kalle, no such user", actual = response.content)
            }
        }
    }

    @Test
    fun `test error when stealer does not exist`() {
        withTestApplication({ module(testing = true) }) {
            highscores["Kalle"] = atomic(0L)
            handleRequest(HttpMethod.Post, "/steal") {
                addHeader("content-type", ContentType.Application.Json.toString())
                setBody(
                    gson.toJson(
                        StealBody("Kalle", "Olle")
                    )
                )
            }.apply {
                // Cannot run without media type
                assertEquals(expected = HttpStatusCode.NotAcceptable, actual = response.status())
                assertEquals(expected = "No such user as Olle", actual = response.content)
            }
        }
    }

    @Test
    fun `test error when both are poor peasants`() {
        withTestApplication({ module(testing = true) }) {
            highscores["Kalle"] = atomic(0L)
            highscores["Olle"] = atomic(0L)
            handleRequest(HttpMethod.Post, "/steal") {
                addHeader("content-type", ContentType.Application.Json.toString())
                setBody(
                    gson.toJson(
                        StealBody("Kalle", "Olle")
                    )
                )
            }.apply {
                // Cannot run without media type
                assertEquals(expected = HttpStatusCode.NotAcceptable, actual = response.status())
                assertEquals(expected = "It is evil to steal from the poor.", actual = response.content)
            }
        }
    }

    @Test
    fun `test cannot steal when people are created equal`() {
        withTestApplication({ module(testing = true) }) {
            highscores["Kalle"] = atomic(101L)
            highscores["Olle"] = atomic(101L)
            handleRequest(HttpMethod.Post, "/steal") {
                addHeader("content-type", ContentType.Application.Json.toString())
                setBody(
                    gson.toJson(
                        StealBody("Kalle", "Olle")
                    )
                )
            }.apply {
                // Cannot run without media type
                assertEquals(expected = HttpStatusCode.NotAcceptable, actual = response.status())
                assertEquals(
                    expected = "One can only steal from someone who has more money than one self.",
                    actual = response.content
                )
            }
        }
    }

    @Test
    fun `test that one can steal from soneone who has more money`() {
        withTestApplication({ module(testing = true) }) {
            highscores["Kalle"] = atomic(102L)
            highscores["Olle"] = atomic(101L)
            handleRequest(HttpMethod.Post, "/steal") {
                addHeader("content-type", ContentType.Application.Json.toString())
                setBody(
                    gson.toJson(
                        StealBody("Kalle", "Olle")
                    )
                )
            }.apply {
                // Cannot run without media type
                assertEquals(expected = HttpStatusCode.OK, actual = response.status())
            }
        }
    }

}
