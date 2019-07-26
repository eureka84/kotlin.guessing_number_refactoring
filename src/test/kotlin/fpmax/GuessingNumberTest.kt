package fpmax

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.extensions.id.monad.monad
import arrow.data.*
import arrow.data.extensions.statet.monad.monad
import arrow.typeclasses.Monad
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class GuessingNumberTest {

    private val monad: Monad<Kind<Kind<ForStateT, ForId>, TestData>> = State.monad(Id.monad())
    private val guessingGame: GuessingGame<Kind<Kind<ForStateT, ForId>, TestData>> = GuessingGame(
        TestConsole(),
        TestRandomNatural(),
        monad
    )
    private val program: State<TestData, Unit> = guessingGame.play().fix()

    @Test
    fun correctFirstGuess() {
        val initial = TestData(inputs = listOf("Angelo", "4", "n"), outputs = listOf(), num = 4)

        val (final: TestData, _) = program.run(initial)

        assertThat(final.outputs, equalTo(listOf(
            "What is your name?",
            "Hello, Angelo, welcome to the game!",
            "Dear Angelo, please guess a number from 1 to 5:",
            "You guessed right, Angelo!",
            "Do you want to continue, Angelo?"
        )))
    }

    @Test
    fun wrongGuesses() {
        val initial = TestData(inputs = listOf("Angelo", "4", "f", "3", "n"), outputs = listOf(), num = 5)

        val (final: TestData, _) = program.run(initial)

        assertThat(final.outputs, equalTo(listOf(
            "What is your name?",
            "Hello, Angelo, welcome to the game!",
            "Dear Angelo, please guess a number from 1 to 5:",
            "You guessed wrong, Angelo! The number was: 5",
            "Do you want to continue, Angelo?",
            "Dear Angelo, please guess a number from 1 to 5:",
            "You guessed wrong, Angelo! The number was: 5",
            "Do you want to continue, Angelo?"
        )))
    }

}

data class TestData(val inputs: List<String>, val outputs: List<String>, val num: Int)

class TestConsole: Console<StatePartialOf<TestData>> {
    override fun writeLn(msg: String): Kind<StatePartialOf<TestData>, Unit> = State { testData: TestData ->
        Tuple2(testData.copy(outputs = testData.outputs + msg), Unit) }.fix()

    override fun readLn(): Kind<StatePartialOf<TestData>, String?> =
        State { testData: TestData -> Tuple2(
            testData.copy(inputs = testData.inputs.subList(1, testData.inputs.size)),
            testData.inputs.first()
        ) }
}

class TestRandomNatural: RandomNatural<StatePartialOf<TestData>> {
    override fun upTo(upper: Int): Kind<StatePartialOf<TestData>, Int> =
        State { testData: TestData -> Tuple2(testData, testData.num)}

}