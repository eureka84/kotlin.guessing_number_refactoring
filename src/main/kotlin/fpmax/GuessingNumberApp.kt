package fpmax

import arrow.Kind
import arrow.core.Try
import arrow.core.getOrElse
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.monad.monad
import arrow.effects.fix
import java.util.*

fun main(args: Array<String>) {
    val random = Try { Random(args[0].toLong()) }.getOrElse { Random() }
    val guessingGame = GuessingGame(ConsoleIO, RandomNaturalIO(random), IO.monad())
    val program: IO<Unit> = guessingGame.play().fix()

    program.unsafeRunSync()
}

object ConsoleIO : Console<ForIO> {
    override fun readLn(): Kind<ForIO, String?> = IO { readLine() }
    override fun writeLn(msg: String): Kind<ForIO, Unit> = IO { println(msg) }
}

class RandomNaturalIO(private val random: Random) : RandomNatural<ForIO> {
    override fun upTo(upper: Int): Kind<ForIO, Int> = IO { random.nextInt(upper) + 1 }
}