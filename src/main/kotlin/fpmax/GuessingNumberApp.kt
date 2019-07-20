package fpmax

import arrow.core.Option
import arrow.core.Try
import arrow.core.getOrElse
import java.util.*


fun main(args: Array<String>) {
    val seed = args[0].toLong()
    val random = Random(seed)

    println("What is your name?")

    val name = readLine()

    println("Hello, $name, welcome to the game!")

    var exec = true

    while (exec) {
        val num = random.nextInt(5) + 1

        println("Dear $name, please guess a number from 1 to 5:")

        val guess = readGuess()
        val guessedRight = guess.map { g -> g == num }.getOrElse { false }

        if (guessedRight) println("You guessed right, $name!")
        else println("You guessed wrong, $name! The number was: $num")

        println("Do you want to continue, $name?")

        exec = checkContinue()
    }
}

private fun readGuess(): Option<Int> =
    readLine()
        .toOption()
        .flatMap { input -> Try { input.toInt() }.toOption() }

private fun <T> T?.toOption(): Option<T> = Option.fromNullable(this)

private fun checkContinue(): Boolean =
    when (readLine()?.toLowerCase()) {
        "y" -> true
        "n" -> false
        else -> true
    }