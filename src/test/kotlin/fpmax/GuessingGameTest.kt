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

class GuessingNumberTest {

    private val guessingGame: GuessingGame<StatePartialOf<TestData>> =
        object : GuessingGame<StatePartialOf<TestData>>, Monad<StatePartialOf<TestData>> by State.monad(Id.monad()) {
            override val console: ConsoleModule.Console<StatePartialOf<TestData>> = TestConsole()
            override val randomNatural: RandomModule.RandomNatural<StatePartialOf<TestData>> = TestRandomNatural()
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

class TestConsole : ConsoleModule.Console<StatePartialOf<TestData>> {
    override fun writeLn(msg: String): Kind<StatePartialOf<TestData>, Unit> =
        State { testData: TestData ->
            Tuple2(testData.copy(outputs = testData.outputs + msg), Unit)
        }

    override fun readLn(): Kind<StatePartialOf<TestData>, String?> =
        State { testData: TestData ->
            Tuple2(
                testData.copy(inputs = testData.inputs.subList(1, testData.inputs.size)),
                testData.inputs.first()
            )
        }
}

class TestRandomNatural : RandomModule.RandomNatural<StatePartialOf<TestData>> {
    override fun upTo(upper: Int): Kind<StatePartialOf<TestData>, Int> =
        State { testData: TestData -> Tuple2(testData, testData.num) }

}