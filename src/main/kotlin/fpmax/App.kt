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
    val guessingGame: GuessingGame<ForIO> = buildGuessingGame(random)
    val program: IO<Unit> = guessingGame.play().fix()

    program.unsafeRunSync()
}

private fun buildGuessingGame(random: Random): GuessingGame<ForIO> {
    return object : GuessingGame<ForIO>,
        Monad<ForIO> by IO.monad(),
        ConsoleModule<ForIO> by ConsoleModuleIO,
        RandomModule<ForIO> by randomModule(random) {}
}

private fun randomModule(random: Random): RandomModule<ForIO> {
    return object : RandomModule<ForIO> {
        override val randomNatural: RandomModule.RandomNatural<ForIO> =
            object : RandomModule.RandomNatural<ForIO> {
                override fun upTo(upper: Int): Kind<ForIO, Int> = IO { random.nextInt(upper) + 1 }
            }
    }
}

object ConsoleModuleIO : ConsoleModule<ForIO> {
    override val console: ConsoleModule.Console<ForIO> =
        object : ConsoleModule.Console<ForIO>, Monad<ForIO> by IO.monad() {
            override fun readLn(): Kind<ForIO, String?> = IO { readLine() }
            override fun writeLn(msg: String): Kind<ForIO, Unit> = IO { println(msg) }
        }
}