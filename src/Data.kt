
data class SendData(val date: String, val highScore: List<PersonScore>, val robinReady: Boolean = Sherwood.ready.value)

data class PersonScore(val name: String, val score: Long)

data class ReceiveData(val uuid: String, val personName: String, val scoreType: ScoreType)

enum class ScoreType {
    INC,
    DEC
}
