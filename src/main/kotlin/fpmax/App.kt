package fpmax

import arrow.Kind
import arrow.core.Try
import arrow.core.getOrElse
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.monad.monad
import arrow.effects.fix
import arrow.typeclasses.Monad
import java.util.*

fun main(args: Array<String>) {
    val random = Try { Random(args[0].toLong()) }.getOrElse { Random() }
    val guessingGame: GuessingGame<ForIO> =
        object : GuessingGame<ForIO>, Monad<ForIO> by IO.monad() {
            override val console: ConsoleModule.Console<ForIO> = ConsoleIO
            override val randomNatural: RandomModule.RandomNatural<ForIO> = RandomNaturalIO(random)
        }

    val program: IO<Unit> = guessingGame.play().fix()

    program.unsafeRunSync()
}


object ConsoleIO : ConsoleModule.Console<ForIO> {
    override fun readLn(): Kind<ForIO, String?> = IO { readLine() }
    override fun writeLn(msg: String): Kind<ForIO, Unit> = IO { println(msg) }
}

class RandomNaturalIO(private val random: Random) : RandomModule.RandomNatural<ForIO> {
    override fun upTo(upper: Int): Kind<ForIO, Int> = IO { random.nextInt(upper) + 1 }
}