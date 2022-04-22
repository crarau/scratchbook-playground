import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.system.measureTimeMillis

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun main() = runBlocking {
    val listener = Listener()
    listener.initialize()
}

class Listener {
    val channel = Channel<User>()

    val externalAPI = ExternalAPI<User>()
    suspend fun initialize() = coroutineScope {
        externalAPI.addListener(object: EventListener<User> {
            override fun onEvent(event: User) {
                launch { channel.send(event) }
            }
        })

        val time = measureTimeMillis {
            repeat(500) {
                launch {
                    delay(50)
                    externalAPI.emit(User(it, "John", 33))
                }
            }
        }
        log("sent events in $time ms")

        for (user in channel) {
            log("from channel id: ${user.id} name: ${user.name}")
            launch { callExternalAPI(user) }
        }
    }
}

suspend fun callExternalAPI(user: User) {
    delay(1000)
    log("from external API id: ${user.id} name: ${user.name}")
}

/** An interface for event listeners.  */
interface EventListener<T> {
    /**
     * onEvent will be called with the new value or the error if an error occurred. It's guaranteed
     * that exactly one of value
     *
     * @param value The value of the event. null if there was an error.
     */
    fun onEvent(value: T)
}

class ExternalAPI<T> {
    private val listeners: MutableList<EventListener<T>> = mutableListOf()
    fun addListener(listener: EventListener<T>) {
        listeners.add(listener)
    }

    fun removeListener(listener: EventListener<T>) {
        listeners.remove(listener)
    }

    fun emit(value: T) {
        listeners.forEach { it.onEvent(value) }
    }
}

data class User(val id: Number, val name: String, val age: Int)

