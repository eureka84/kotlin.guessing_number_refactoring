package fpmax

import arrow.Kind
import arrow.typeclasses.Monad

interface ConsoleModule<E> {
    val console: Console<E>

    interface Console<E> : Monad<E> {
        fun writeLn(msg: String): Kind<E, Unit>
        fun readLn(): Kind<E, String?>
        fun ask(question: String): Kind<E, String?> = writeLn(question).flatMap { readLn() }
    }
}