import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant

const val cooldown = 30000L
const val charityName = "Charity"

object Sherwood {
    val ready = atomic(true)
}

fun Route.robinHood() {
    get("/robinHood") {
        val now = Instant.now()
        if (Sherwood.ready.compareAndSet(expect = true, update = false)) {
            call.respond(HttpStatusCode.Accepted)
            val copyOfList = highscores.toList().sortedByDescending { it.second.value }
            // Make ready again in 30 seconds.
            launch {
                delay(cooldown) // Cooldown before reset.
                Sherwood.ready.compareAndSet(expect = false, update = true)
            }
            // Take from top three, give to rest.
            var sumOfStealie = 0
            // Steal X percent from top 3
            // Distribute to all the poor people.
            val listSize = copyOfList.size
            if (listSize <= 3) {
                var sumOfRobberies: Long = 0
                for (i in 0 until listSize) {
                    val gettingRobbed = copyOfList[i]
                    if (gettingRobbed.first == charityName) continue
                    val riches = gettingRobbed.second.value
                    val robbery = riches.div(3)
                    highscores[gettingRobbed.first]?.minusAssign(robbery)
                    sumOfRobberies += robbery
                }
                if (sumOfRobberies > 0) {
                    highscores.computeIfAbsent(charityName) {
                        atomic(0L)
                    } += sumOfRobberies
                }
            } else {
                // Get from 3 riches
                var sumOfRobberies: Long = 0
                for (i in 0 until 3) {
                    val gettingRobbed = copyOfList[i]
                    if (gettingRobbed.first == charityName) continue
                    val riches = gettingRobbed.second.value
                    val robbery = riches.div(3)
                    highscores[gettingRobbed.first]?.minusAssign(robbery)
                    sumOfRobberies += robbery
                }
                val divides = copyOfList.size - 3
                val theirCut = sumOfRobberies / divides // Robin hood steals money when can't devise equally.
                copyOfList.subList(3, listSize).forEach {
                    it.second.addAndGet(theirCut)
                }
            }
        } else {
            call.respond(HttpStatusCode.OK) // Do nothing.
        }
    }
}
