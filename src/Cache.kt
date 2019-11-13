import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class ShittySet<K> constructor() : MutableSet<K> by Collections.synchronizedSet(mutableSetOf()) {
    init {
        GlobalScope.launch {
            while (true) {
                this@ShittySet.clear()
                delay(5000)
            }
        }
    }

}
