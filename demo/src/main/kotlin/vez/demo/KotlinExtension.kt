package vez.demo

import java.util.*

fun main(args: Array<String>) {

    applyAction("hello", "bye") { s: String -> println(s.uuid()) }
    // println("hello".uuid())
}

fun applyAction(vararg s: String, action: (String) -> Unit) {
    s.forEach(action)
}

fun String.uuid(): String = UUID.nameUUIDFromBytes(this.encodeToByteArray()).toString()
