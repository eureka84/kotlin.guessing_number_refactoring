package fpmax

import arrow.Kind
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.monad.monad
import arrow.effects.fix
import java.util.*


fun main(args: Array<String>) {
    val seed = args[0].toLong()
    val random = Random(seed)

    val guessingGame = GuessingGame(ConsoleIO, RandomIntIO(random), IO.monad())

    val program: IO<Unit> = guessingGame.play().fix()

    program.unsafeRunSync()
}

object ConsoleIO : Console<ForIO> {
    override fun readLn(): Kind<ForIO, String?> = IO { readLine() }
    override fun writeLn(msg: String): Kind<ForIO, Unit> = IO { println(msg) }
}

class RandomIntIO(private val random: Random) : RandomInt<ForIO> {
    override fun next(upper: Int): Kind<ForIO, Int> = IO { random.nextInt(upper) }
}