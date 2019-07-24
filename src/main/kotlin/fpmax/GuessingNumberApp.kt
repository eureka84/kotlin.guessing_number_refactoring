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

    val program: IO<Unit> = GuessingGame(ConsoleIO, RandomIO(random), IO.monad()).play().fix()

    program.unsafeRunSync()
}

object ConsoleIO : Console<ForIO> {
    override fun readLn(): Kind<ForIO, String?> = IO { readLine() }
    override fun writeLn(msg: String): Kind<ForIO, Unit> = IO { println(msg) }
}

class RandomIO(private val random: Random) : CustomRandom<ForIO> {
    override fun nextInt(upper: Int): Kind<ForIO, Int> = IO { random.nextInt(upper) }
}