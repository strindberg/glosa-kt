package se.jh.glosa.txtui

import se.jh.glosa.fw.Glosa
import se.jh.glosa.fw.IWordChooser
import se.jh.glosa.fw.SuccessiveRandomWordChooser
import java.io.BufferedReader
import java.io.InputStreamReader

class TextController(private val logic: Glosa, private val inverse: Boolean) {

    val ANSI_RESET = "\u001B[0m"
    val ANSI_RED = "\u001B[31m"
    val ANSI_CLRSCREEN = "\u001B[H"
    val ANSI_HOME = "\u001B[2J"

    val PRINT_WIDTH = 60

    private val reader = BufferedReader(InputStreamReader(System.`in`))

    private val chooser: IWordChooser = SuccessiveRandomWordChooser(logic.words)

    fun go(oneShot: Boolean) {
        clearScreen()
        do {
            val currentWord = chooser.nextIWord(inverse)

            val question = currentWord.getQuestion(inverse)
            printQuestion(question)

            val answer = reader.readLine()
            if (answer == "xxx") {
                logic.quit(true)
            } else {
                clearScreen()
                printQuestion(question)
                println(answer)
                print("%-${PRINT_WIDTH}s".format(currentWord.getAnswer(inverse)))
                if (!currentWord.isCorrect(answer, inverse)) {
                    print("${ANSI_RED}INCORRECT!${ANSI_RESET}")
                }
                println("\n")
            }
        } while (!oneShot)
        logic.quit(true)
    }

    private fun clearScreen() {
        println("${ANSI_CLRSCREEN}${ANSI_HOME}")
    }

    private fun printQuestion(question: String) {
        print("%-${PRINT_WIDTH}s".format(question))
        println(chooser.noToChooseAmong())
    }

}
