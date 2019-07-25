package fpmax

import arrow.Kind
import arrow.core.Option
import arrow.core.Try
import arrow.core.extensions.option.traverse.sequence
import arrow.typeclasses.Monad

interface Console<E> {
    fun writeLn(msg: String): Kind<E, Unit>
    fun readLn(): Kind<E, String?>
}

interface CustomRandom<E> {
    fun upTo(upper: Int): Kind<E, Int>
}

class GuessingGame<E>(
    private val console: Console<E>,
    private val random: CustomRandom<E>,
    private val monad: Monad<E>
) {
    fun play(): Kind<E, Unit> = monad.run {
        console
            .writeLn("What is your name?")
            .flatMap { console.readLn() }
            .flatMap { name ->
                console
                    .writeLn("Hello, $name, welcome to the game!")
                    .flatMap { gameLoop(name) }
            }
    }

    private fun gameLoop(player: String?): Kind<E, Unit> = monad.run {
        random
            .upTo(5)
            .flatMap { num -> askPlayerToGuess(player, num) }
    }

    private fun askPlayerToGuess(name: String?, num: Int): Kind<E, Unit> = monad.run {
        console
            .writeLn("Dear $name, please guess a number from 1 to 5:")
            .flatMap { readGuess() }
            .flatMap { guess -> evaluateGuess(guess, num, name) }
            .flatMap { console.writeLn("Do you want to continue, $name?") }
            .flatMap { checkContinue({ monad.just(Unit) }, { gameLoop(name) }) }
    }


    private fun evaluateGuess(guess: Option<Int>, num: Int, name: String?) =
        guess
            .map { numberGuessed -> numberGuessed == num }
            .map { guessedRight ->
                if (guessedRight)
                    console.writeLn("You guessed right, $name!")
                else
                    console.writeLn("You guessed wrong, $name! The number was: $num")
            }.sequence(monad)

    private fun readGuess(): Kind<E, Option<Int>> = monad.run {
        console.readLn().map { input -> Try { input!!.toInt() }.toOption() }
    }

    private fun checkContinue(ifNo: () -> Kind<E, Unit>, ifYes: () -> Kind<E, Unit>) = monad.run {
        console.readLn().flatMap { input ->
            when (input?.toLowerCase()) {
                "y" -> ifYes()
                "n" -> ifNo()
                else -> ifYes()
            }
        }
    }
}