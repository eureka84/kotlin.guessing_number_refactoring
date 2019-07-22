package fpmax

import arrow.core.Option
import arrow.core.Try
import arrow.core.getOrElse
import arrow.effects.IO
import arrow.effects.extensions.io.monad.monad
import arrow.effects.fix
import java.util.*


fun main(args: Array<String>) {
    val seed = args[0].toLong()
    val random = Random(seed)

    program(random).unsafeRunSync()

}

private fun program(random: Random): IO<Unit> =
    IO.monad().binding { // TODO FIXME using fx
        putStrLine("What is your name?").bind()
        val name = readStrLine().bind()
        putStrLine("Hello, $name, welcome to the game!").bind()
        gameLoop(random, name).bind()
    }.fix()

private fun gameLoop(random: Random, name: String?): IO<Unit> =
    IO.monad().binding { // TODO FIXME using fx
        val num = IO { random.nextInt(5) + 1 }.bind()
        putStrLine("Dear $name, please guess a number from 1 to 5:").bind()
        val guessedRight = readGuess().bind().map { g -> g == num }.getOrElse { false }
        if (guessedRight) putStrLine("You guessed right, $name!").bind()
        else putStrLine("You guessed wrong, $name! The number was: $num").bind()
        putStrLine("Do you want to continue, $name?").bind()
        val cont = checkContinue().bind()
        if (cont) gameLoop(random, name).bind()
        else IO { Unit }.bind()
    }.fix()

fun putStrLine(message: String): IO<Unit> = IO { println(message) }
fun readStrLine(): IO<String?> = IO { readLine() }

fun readGuess(): IO<Option<Int>> =
    readStrLine().map { input -> Try { input!!.toInt() }.toOption() }

fun checkContinue(): IO<Boolean> =
    readStrLine().map { input ->
        when (input?.toLowerCase()) {
            "y" -> true
            "n" -> false
            else -> true
        }
    }
