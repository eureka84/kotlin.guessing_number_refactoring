package fpmax

import arrow.Kind
import arrow.core.Try
import arrow.typeclasses.Monad

interface Console<E> {
    fun writeLn(msg: String): Kind<E, Unit>
    fun readLn(): Kind<E, String?>
}

interface RandomNatural<E> {
    fun upTo(upper: Int): Kind<E, Int>
}

class GuessingGame<E>(
    private val console: Console<E>,
    private val randomNatural: RandomNatural<E>,
    private val monad: Monad<E>
) {
    fun play(): Kind<E, Unit> = monad.run {
        console
            .ask("What is your name?")
            .flatMap { name ->
                console
                    .writeLn("Hello, $name, welcome to the game!")
                    .flatMap { gameLoop(name) }
            }
    }

    private fun gameLoop(player: String?): Kind<E, Unit> = monad.run {
        randomNatural
            .upTo(5)
            .flatMap { num -> askAndEvaluatePlayerGuess(player, num) }
            .flatMap { checkContinue(player, { monad.just(Unit) }, { gameLoop(player) }) }
    }

    private fun askAndEvaluatePlayerGuess(player: String?, num: Int) = monad.run {
        readGuess(player)
            .flatMap { guess -> evaluateGuess(guess, num, player) }
    }

    private fun readGuess(player: String?): Kind<E, Int> = monad.run {
        console
            .ask("Dear $player, please guess a number from 1 to 5:")
            .flatMap { guess ->
                Try { guess!!.toInt() }.fold(
                    {
                        console
                            .writeLn("Dear $player you have not entered a number")
                            .flatMap { readGuess(player) }
                    },
                    { monad.just(it) }
                )
            }
    }

    private fun evaluateGuess(guess: Int, num: Int, player: String?): Kind<E, Unit> =
        if (guess == num)
            console.writeLn("You guessed right, $player!")
        else
            console.writeLn("You guessed wrong, $player! The number was: $num")

    private fun checkContinue(
        player: String?, ifNo: () -> Kind<E, Unit>, ifYes: () -> Kind<E, Unit>
    ): Kind<E, Unit> = monad.run {
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

    private fun Console<E>.ask(question: String): Kind<E, String?> = monad.run {
        writeLn(question).flatMap { readLn() }
    }
}