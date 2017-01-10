package se.jh.glosa.txtui

import se.jh.glosa.fw.Glosa
import se.jh.glosa.fw.IWordChooser
import se.jh.glosa.fw.SuccessiveRandomWordChooser
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class TextController(private val logic: Glosa, private val inverse: Boolean) {

    private val reader = BufferedReader(InputStreamReader(System.`in`))

    private val chooser: IWordChooser = SuccessiveRandomWordChooser(logic.words)

    // TODO: constant
    private val printWidth = 80

    fun go(oneShot: Boolean) {
        try {
            do {
                println()
                val currentWord = chooser.nextIWord(inverse)

                val questionWidth = currentWord.printQuestion(System.out, inverse)
                for (i in 0..printWidth - questionWidth - 1) {
                    print(" ")
                }
                System.out.print(chooser.noToChooseAmong())
                println()

                val answer = reader.readLine()
                if (answer == "xxx") {
                    logic.quit(true)
                } else {
                    val wordWidth = currentWord.printAnswer(System.out, inverse)

                    if (!currentWord.isCorrect(answer, inverse)) {
                        for (i in 0..printWidth - wordWidth - 1) {
                            print(" ")
                        }
                        print("INCORRECT!")
                    }
                    println("\n")
                }
            } while (!oneShot)
            logic.quit(true)
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            System.exit(1)
        }
    }

}
