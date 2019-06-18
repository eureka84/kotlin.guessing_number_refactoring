package fpmax

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream

class GuessingNumberAppKtTest {

    @Test
    fun correctFirstGuess() {
        val swapStreams = { inputStream: InputStream, printStream: PrintStream ->
            System.setIn(inputStream)
            System.setOut(printStream)
        }
        val initialOut = System.out
        val initialIn = System.`in`
        val byteArrayOutputStream = ByteArrayOutputStream()
        val inputs = listOf(
            "Angelo",
            "4",
            "n"
        ).joinToString(System.getProperty("line.separator"))
        swapStreams(
            ByteArrayInputStream(inputs.toByteArray()),
            PrintStream(byteArrayOutputStream)
        )

        val seed = "2"
        main(arrayOf(seed))

        swapStreams(initialIn, initialOut)

        assertThat(
            byteArrayOutputStream.toString(), equalTo(
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
        val swapStreams = { inputStream: InputStream, printStream: PrintStream ->
            System.setIn(inputStream)
            System.setOut(printStream)
        }
        val initialOut = System.out
        val initialIn = System.`in`
        val byteArrayOutputStream = ByteArrayOutputStream()
        val inputs = listOf(
            "Angelo",
            "4",
            "f",
            "3",
            "n"
        ).joinToString(System.getProperty("line.separator"))
        swapStreams(
            ByteArrayInputStream(inputs.toByteArray()),
            PrintStream(byteArrayOutputStream)
        )

        val seed = "3"
        main(arrayOf(seed))

        swapStreams(initialIn, initialOut)

        assertThat(
            byteArrayOutputStream.toString(), equalTo(
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