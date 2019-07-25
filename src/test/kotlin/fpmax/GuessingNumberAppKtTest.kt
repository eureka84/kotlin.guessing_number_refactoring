package fpmax

import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream

class GuessingNumberAppKtTest {

    val swapStreams = { inputStream: InputStream, printStream: PrintStream ->
        System.setIn(inputStream)
        System.setOut(printStream)
    }
    lateinit var initialOut: PrintStream
    lateinit var initialIn: InputStream
    lateinit var outputStream: ByteArrayOutputStream


    @Before
    fun setUp() {
        initialOut = System.out
        initialIn = System.`in`
        outputStream = ByteArrayOutputStream()
    }
    @After
    fun tearDown() {
        swapStreams(initialIn, initialOut)
    }

    @Test
    fun correctFirstGuess() {
        val inputs = listOf(
            "Angelo",
            "4",
            "n"
        ).joinToString(System.getProperty("line.separator"))
        swapStreams(
            ByteArrayInputStream(inputs.toByteArray()),
            PrintStream(outputStream)
        )

        main(arrayOf("2"))

        assertThat(
            outputStream.toString(), equalTo(
                "What is your name?\n" +
                        "Hello, Angelo, welcome to the game!\n" +
                        "Dear Angelo, please guess a number from 1 to 5:\n" +
                        "You guessed right, Angelo!\n" +
                        "Do you want to continue, Angelo?\n"
            )
        )
    }

    @Test
    fun wrongGuesses() {
        val inputs = listOf(
            "Angelo",
            "4",
            "f",
            "3",
            "n"
        ).joinToString(System.getProperty("line.separator"))
        swapStreams(
            ByteArrayInputStream(inputs.toByteArray()),
            PrintStream(outputStream)
        )

        main(arrayOf("3"))

        assertThat(
            outputStream.toString(), equalTo(
                "What is your name?\n" +
                        "Hello, Angelo, welcome to the game!\n" +
                        "Dear Angelo, please guess a number from 1 to 5:\n" +
                        "You guessed wrong, Angelo! The number was: 5\n" +
                        "Do you want to continue, Angelo?\n" +
                        "Dear Angelo, please guess a number from 1 to 5:\n" +
                        "You guessed wrong, Angelo! The number was: 1\n" +
                        "Do you want to continue, Angelo?\n"
            )
        )
    }
}