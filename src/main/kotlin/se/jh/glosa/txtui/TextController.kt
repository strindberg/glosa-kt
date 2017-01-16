package se.jh.glosa.txtui

import org.jline.reader.LineReader.EMACS
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.reader.impl.LineReaderImpl
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import se.jh.glosa.fw.Glosa
import se.jh.glosa.fw.IWordChooser
import se.jh.glosa.fw.SuccessiveRandomWordChooser
import se.jh.glosa.vo.IWord

// TODO: kunna starta utan specialpath

class TextController(private val logic: Glosa, private val inverse: Boolean) {

    val ANSI_RESET = "\u001B[0m"
    val ANSI_RED = "\u001B[31m"
    val ANSI_GREEN = "\u001B[32m"
    val ANSI_CLRSCRN = "\u001B[H"
    val ANSI_HOME = "\u001B[2J"
    val ANSI_HIDE_CURSOR = "\u001b[?25l"
    val ANSI_SHOW_CURSOR = "\u001b[?25h"

    val PRINT_WIDTH = 60

    private val chooser: IWordChooser = SuccessiveRandomWordChooser(logic.words)

    private val terminal = TerminalBuilder.builder().nativeSignals(true).signalHandler(Terminal.SignalHandler.SIG_IGN).build()

    private val reader = LineReaderBuilder.builder().terminal(terminal).build() as LineReaderImpl

    init {
        reader.setKeyMap(EMACS)
    }

    fun go(oneShot: Boolean) {
        do {
            val currentWord = chooser.nextIWord(inverse)

            clearScreen()
            showCursor()
            printlnQuestion(currentWord.getQuestion(inverse))

            try {
                val answer = reader.readLine()
                printAnswer(currentWord)
                if (currentWord.isCorrect(answer, inverse)) {
                    printlnFeedback("CORRECT!", ANSI_GREEN)
                } else {
                    printlnFeedback("INCORRECT!", ANSI_RED)
                }
            } catch (e: UserInterruptException) {
                quit()
            }

            hideCursor()
            reader.readLine()
        } while (!oneShot)
        logic.quit(true)
    }

    private fun quit() {
        clearScreen()
        logic.quit(true)
    }

    private fun clearScreen() {
        print("${ANSI_CLRSCRN}${ANSI_HOME}")
    }

    private fun printlnQuestion(question: String) {
        print("%-${PRINT_WIDTH}s".format(question))
        println(chooser.noToChooseAmong())
    }

    private fun printAnswer(currentWord: IWord) {
        print("%-${PRINT_WIDTH}s".format(currentWord.getAnswer(inverse)))
    }

    private fun printlnFeedback(string: String, color: String) {
        println("${color}${string}${ANSI_RESET}")
    }

    private fun hideCursor() {
        print(ANSI_HIDE_CURSOR)
    }

    private fun showCursor() {
        print(ANSI_SHOW_CURSOR)
    }

}
