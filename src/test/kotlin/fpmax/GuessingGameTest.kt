package fpmax

import arrow.Kind
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.extensions.id.monad.monad
import arrow.data.State
import arrow.data.StatePartialOf
import arrow.data.extensions.statet.monad.monad
import arrow.data.fix
import arrow.data.run
import arrow.typeclasses.Monad
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

typealias TestState = StatePartialOf<TestData>

class GuessingNumberTest {

    private val guessingGame: GuessingGame<TestState> =
        object : GuessingGame<TestState>, Monad<TestState> by State.monad(Id.monad()) {
            override val console: ConsoleModule.Console<TestState> = TestConsole()
            override val randomNatural: RandomModule.RandomNatural<TestState> = TestRandomNatural()
        }

    private val program: State<TestData, Unit> = guessingGame.play().fix()

    @Test
    fun correctFirstGuess() {
        val initial = TestData(inputs = listOf("Angelo", "4", "n"), outputs = listOf(), num = 4)

        val (final: TestData, _) = program.run(initial)

        assertThat(
            final.outputs, equalTo(
                listOf(
                    "What is your name?",
                    "Hello, Angelo, welcome to the game!",
                    "Dear Angelo, please guess a number from 1 to 5:",
                    "You guessed right, Angelo!",
                    "Do you want to continue, Angelo?"
                )
            )
        )
    }

    @Test
    fun wrongGuesses() {
        val initial = TestData(inputs = listOf("Angelo", "4", "f", "y", "s", "3", "n"), outputs = listOf(), num = 5)

        val (final: TestData, _) = program.run(initial)

        assertThat(
            final.outputs, equalTo(
                listOf(
                    "What is your name?",
                    "Hello, Angelo, welcome to the game!",
                    "Dear Angelo, please guess a number from 1 to 5:",
                    "You guessed wrong, Angelo! The number was: 5",
                    "Do you want to continue, Angelo?",
                    "Dear Angelo enter y/n",
                    "Do you want to continue, Angelo?",
                    "Dear Angelo, please guess a number from 1 to 5:",
                    "Dear Angelo you have not entered a number",
                    "Dear Angelo, please guess a number from 1 to 5:",
                    "You guessed wrong, Angelo! The number was: 5",
                    "Do you want to continue, Angelo?"
                )
            )
        )
    }

}

data class TestData(val inputs: List<String>, val outputs: List<String>, val num: Int)

class TestConsole : ConsoleModule.Console<TestState>, Monad<TestState> by State.monad(Id.monad()) {
    override fun writeLn(msg: String): Kind<TestState, Unit> =
        State { testData: TestData ->
            Tuple2(testData.copy(outputs = testData.outputs + msg), Unit)
        }

    override fun readLn(): Kind<TestState, String?> =
        State { testData: TestData ->
            Tuple2(
                testData.copy(inputs = testData.inputs.subList(1, testData.inputs.size)),
                testData.inputs.first()
            )
        }
}

class TestRandomNatural : RandomModule.RandomNatural<TestState> {
    override fun upTo(upper: Int): Kind<TestState, Int> =
        State { testData: TestData -> Tuple2(testData, testData.num) }

}