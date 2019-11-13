import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.atomicfu.AtomicLong
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val shittyCache = ShittySet<String>()
val highscores: MutableMap<String, AtomicLong> = ConcurrentHashMap()

val myNicerDateFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("EEE, dd MMM yyyy HH:mm:ss z")
    .withLocale(Locale.UK)
    .withZone(ZoneId.systemDefault())

val gson = Gson()

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    install(StatusPages) {
        exception<Exception> { exception ->
            call.respond(status = HttpStatusCode.BadRequest, message = "Request threw exception: $exception")
        }
    }

    routing {
        get("/") {
            call.respondHtml { shittyHtml() }
        }

        static("/static") {
            resources("static")
        }

        post("/score") {
            val newData = call.receive<ReceiveData>()
            if (shittyCache.add(newData.uuid)) {
                val (_, who, what) = newData
                highscores.computeIfAbsent(who.trim()) {
                    atomic(0L)
                } += when (what) {
                    ScoreType.INC -> 1
                    ScoreType.DEC -> -1
                }
                delay(1000)
                call.respond(status = HttpStatusCode.OK, message = Unit)
            } else {
                delay(10000)
                call.respond(status = HttpStatusCode.Conflict, message = "UUID reuse detected!")
            }
        }

        // Install steal route.
        stealRoute()

        // Robin hood
        robinHood()

        // Web socket, to get data transfer over a nice connection
        webSocket("/myws/echo") {
            while (true) {
                delay(100)

                val highscoreList = highscores
                    .toList()
                    .sortedByDescending {
                        it.second.value
                    }
                    .take(50)
                    .map {
                        PersonScore(it.first, it.second.value);
                    }


                val biggieMcScoreFace = SendData(
                    date = myNicerDateFormatter.format(Instant.now()),
                    highScore = highscoreList
                )
                send(Frame.Text(gson.toJson(biggieMcScoreFace)))
            }
        }

    }
}
