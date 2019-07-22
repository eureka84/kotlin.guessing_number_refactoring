package fpmax

import arrow.core.Option
import arrow.core.Try
import arrow.core.getOrElse
import arrow.effects.IO
import java.util.*


fun main(args: Array<String>) {
    val seed = args[0].toLong()
    val random = Random(seed)

    putStrLine("What is your name?").unsafeRunSync()

    val name = readStrLine().unsafeRunSync()

    putStrLine("Hello, $name, welcome to the game!").unsafeRunSync()

    gameLoop(random, name)
}

private fun gameLoop(random: Random, name: String?) {
    var exec = true

    while (exec) {
        val num = random.nextInt(5) + 1

        putStrLine("Dear $name, please guess a number from 1 to 5:").unsafeRunSync()

        val guess = readGuess()
        val guessedRight = guess.map { g -> g == num }.getOrElse { false }

        if (guessedRight) putStrLine("You guessed right, $name!").unsafeRunSync()
        else putStrLine("You guessed wrong, $name! The number was: $num").unsafeRunSync()

        putStrLine("Do you want to continue, $name?").unsafeRunSync()

        exec = checkContinue()
    }
}

fun putStrLine(message: String): IO<Unit> = IO { println(message) }
fun readStrLine(): IO<String?> = IO { readLine()}

fun readGuess(): Option<Int> =
    readStrLine().unsafeRunSync().let { input -> Try { input!!.toInt() }.toOption() }

fun checkContinue(): Boolean =
    when (readLine()?.toLowerCase()) {
        "y" -> true
        "n" -> false
        else -> true
    }