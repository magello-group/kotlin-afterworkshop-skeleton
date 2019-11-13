import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import kotlinx.atomicfu.AtomicLong

const val stealWeight: Long = 10

fun Route.stealRoute() {
    post("/steal") {
        val stealBody = call.receive<StealBody>()
        val (fromName, toName) = stealBody
        val fromPoints: AtomicLong? = highscores[fromName]
        val toPoints: AtomicLong? = highscores[toName]
        if (fromPoints == null) {
            call.respond(status = HttpStatusCode.NotAcceptable, message = "Cannot steal from $fromName, no such user")
            return@post
        }
        if (toPoints == null) {
            call.respond(status = HttpStatusCode.NotAcceptable, message = "No such user as $toName")
            return@post
        }
        if (fromPoints.value <= stealWeight * 10) {
            call.respond(
                status = HttpStatusCode.NotAcceptable,
                message = "It is evil to steal from the poor."
            )
            return@post
        }
        if (fromPoints.value <= toPoints.value) {
            call.respond(
                status = HttpStatusCode.NotAcceptable,
                message = "One can only steal from someone who has more money than one self."
            )
            return@post
        }
        fromPoints.minusAssign(stealWeight)
        toPoints.plusAssign(stealWeight)
        call.respond(HttpStatusCode.OK)
    }
}

internal data class StealBody(
    val fromName: String,
    val toName: String
)
