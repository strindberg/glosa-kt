package se.jh.glosa.txtui

import org.jline.reader.LineReader.EMACS
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.reader.impl.LineReaderImpl
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import se.jh.glosa.fw.Glosa
import se.jh.glosa.fw.WordChooser
import se.jh.glosa.fw.RandomWordChooser
import se.jh.glosa.vo.Word

const val ANSI_RESET = "\u001B[0m"
const val ANSI_RED = "\u001B[31m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_CLRSCR = "\u001B[H"
const val ANSI_HOME = "\u001B[2J"
const val ANSI_HIDE_CURSOR = "\u001b[?25l"
const val ANSI_SHOW_CURSOR = "\u001b[?25h"

const val PRINT_WIDTH = 60

class TextController(private val logic: Glosa, private val inverse: Boolean) {

    private val chooser: WordChooser = RandomWordChooser(logic.words)

    private val terminal = TerminalBuilder.builder().nativeSignals(true).signalHandler(Terminal.SignalHandler.SIG_IGN).build()

    private val reader = LineReaderBuilder.builder().terminal(terminal).build() as LineReaderImpl

    init {
        reader.setKeyMap(EMACS)
    }

    fun go() {
        while (true) {
            try {
                val currentWord = chooser.nextIWord(inverse)

                clearScreen()
                showCursor()
                printlnQuestion(currentWord.getQuestion(inverse))

                val answer = reader.readLine()
                printAnswer(currentWord)
                if (currentWord.isCorrect(answer, inverse)) {
                    printlnFeedback("CORRECT!", ANSI_GREEN)
                } else {
                    printlnFeedback("INCORRECT!", ANSI_RED)
                }

                hideCursor()
                reader.readLine()
            } catch (e: UserInterruptException) {
                quit()
            }
        }
    }

    private fun quit() {
        clearScreen()
        showCursor()
        logic.quit(true)
    }

    private fun clearScreen() {
        print("${ANSI_CLRSCR}${ANSI_HOME}")
    }

    private fun printlnQuestion(question: String) {
        print("%-${PRINT_WIDTH}s".format(question))
        println(chooser.noToChooseAmong())
    }

    private fun printAnswer(currentWord: Word) {
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
