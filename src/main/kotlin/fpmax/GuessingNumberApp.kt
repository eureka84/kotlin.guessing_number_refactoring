package fpmax

import arrow.core.Option
import arrow.core.Try
import arrow.core.getOrElse
import arrow.effects.IO
import java.util.*


fun main(args: Array<String>) {
    val seed = args[0].toLong()
    val random = Random(seed)

    program(random).unsafeRunSync()

}

private fun program(random: Random): IO<Unit> =
    putStrLine("What is your name?")
        .flatMap { readStrLine() }
        .flatMap { name ->
            putStrLine("Hello, $name, welcome to the game!")
                .flatMap { gameLoop(random, name) }
        }

private fun gameLoop(random: Random, name: String?): IO<Unit> =
    IO { random.nextInt(5) + 1 }
        .flatMap { num ->
            putStrLine("Dear $name, please guess a number from 1 to 5:")
                .flatMap { readGuess() }
                .flatMap { guess -> evaluateGuess(guess, num, name) }
                .flatMap { putStrLine("Do you want to continue, $name?") }
                .flatMap { checkContinue() }
                .flatMap { cont ->
                    if (cont) gameLoop(random, name)
                    else IO { Unit }
                }
        }

private fun evaluateGuess(guess: Option<Int>, num: Int, name: String?): IO<Unit> =
    guess.map { g -> g == num }.getOrElse { false }.let { guessedRight ->
        if (guessedRight) putStrLine("You guessed right, $name!")
        else putStrLine("You guessed wrong, $name! The number was: $num")
    }

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
