package se.jh.glosa.txtui

import se.jh.glosa.fw.Glosa
import se.jh.glosa.fw.IWordChooser
import se.jh.glosa.fw.SuccessiveRandomWordChooser
import java.io.BufferedReader
import java.io.InputStreamReader

class TextController(private val logic: Glosa, private val inverse: Boolean) {

    private val reader = BufferedReader(InputStreamReader(System.`in`))

    private val chooser: IWordChooser = SuccessiveRandomWordChooser(logic.words)

    // TODO: constant
    private val printWidth = 80

    fun go(oneShot: Boolean) {
        do {
            println()
            val currentWord = chooser.nextIWord(inverse)

            currentWord.printQuestion(System.out, inverse, printWidth)
            println(chooser.noToChooseAmong())

            val answer = reader.readLine()
            if (answer == "xxx") {
                logic.quit(true)
            } else {
                currentWord.printAnswer(System.out, inverse, printWidth)
                if (!currentWord.isCorrect(answer, inverse)) {
                    println("INCORRECT!")
                }
                println()
            }
        } while (!oneShot)
        logic.quit(true)
    }

}
