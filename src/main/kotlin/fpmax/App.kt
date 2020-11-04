package fpmax

import arrow.Kind
import arrow.core.Either
import arrow.core.getOrElse
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.coroutines.Environment
import arrow.fx.extensions.io.monad.monad
import arrow.fx.fix
import arrow.typeclasses.Monad
import java.util.*

fun main(args: Array<String>) {
    Environment().unsafeRunSync {
        mainIO(args).unsafeRunSync()
    }
}

private suspend fun mainIO(args: Array<String>): IO<Unit> {
    val random = Either.catch { Random(args[0].toLong()) }.getOrElse { Random() }
    val guessingGame: GuessingGame<ForIO> = buildGuessingGame(random)
    return guessingGame.play().fix()
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