package se.jh.glosa.fw

import se.jh.glosa.txtui.TextController
import se.jh.glosa.vo.IWord


class Glosa(fileName: String) {

    private val fileReader: WordFileReader = WordFileReader(fileName)

    val words: List<IWord> = fileReader.words

    fun quit(save: Boolean) {
        if (save) {
            fileReader.saveHistory(words)
        }
        System.exit(0)
    }

}

fun main(args: Array<String>) {
    var inverse = false
    var oneShot = false
    var fileName: String? = null

    for (arg in args) {
        if (arg == "-i") {
            inverse = true
        } else if (arg == "-o") {
            oneShot = true
        } else {
            fileName = arg
        }
    }

    if (fileName != null) {
        val glosa = Glosa(fileName)
        val controller = TextController(glosa, inverse)
        controller.go(oneShot)
    } else {
        println("Usage: glosa <-i> <-o> [ordfil]")
        System.exit(1)
    }
}
