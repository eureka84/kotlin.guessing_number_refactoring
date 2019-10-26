package fpmax

import arrow.Kind
import arrow.core.Try
import arrow.typeclasses.Monad

interface GuessingGame<E> : Monad<E>, ConsoleModule<E>, RandomModule<E> {

    fun play(): Kind<E, Unit> =
        console
            .ask("What is your name?")
            .flatMap { name ->
                console
                    .writeLn("Hello, $name, welcome to the game!")
                    .flatMap { gameLoop(name) }
            }

    private fun gameLoop(player: String?): Kind<E, Unit> =
        randomNatural
            .upTo(5)
            .flatMap { num -> playerGuess(player, num) }
            .flatMap { checkContinue(player, { just(Unit) }, { gameLoop(player) }) }

    fun playerGuess(player: String?, num: Int) =
        readGuess(player).flatMap { guess -> evaluateGuess(guess, num, player) }

    private fun readGuess(player: String?): Kind<E, Int> =
        console
            .ask("Dear $player, please guess a number from 1 to 5:")
            .flatMap { guess -> parseGuess(guess, player) }

    private fun parseGuess(guess: String?, player: String?): Kind<E, Int> =
        Try { guess!!.toInt() }
            .fold(
                { console.writeLn("Dear $player you have not entered a number").flatMap { readGuess(player) } },
                { just(it) }
            )

    private fun evaluateGuess(guess: Int, num: Int, player: String?): Kind<E, Unit> =
        if (guess == num)
            console.writeLn("You guessed right, $player!")
        else
            console.writeLn("You guessed wrong, $player! The number was: $num")

    private fun checkContinue(player: String?, ifNo: () -> Kind<E, Unit>, ifYes: () -> Kind<E, Unit>): Kind<E, Unit> =
        console
            .ask("Do you want to continue, $player?")
            .flatMap { ans ->
                when (ans?.toLowerCase()) {
                    "y" -> ifYes()
                    "n" -> ifNo()
                    else -> {
                        console
                            .writeLn("Dear $player enter y/n")
                            .flatMap { checkContinue(player, ifNo, ifYes) }
                    }
                }
            }

}