package se.jh.glosa.fw

import se.jh.glosa.txtui.TextController
import se.jh.glosa.vo.Word

class Glosa(fileName: String) {

    private val fileReader: WordFileReader = WordFileReader(fileName)

    val words: List<Word> = fileReader.words

    fun quit(save: Boolean) {
        if (save) {
            fileReader.saveHistory(words)
        }
        System.exit(0)
    }

    fun save() {
        fileReader.saveHistory(words)
    }

}

fun main(args: Array<String>) {
    var inverse = false
    var fileName: String? = null

    for (arg in args) {
        if (arg == "-i") {
            inverse = true
        } else {
            fileName = arg
        }
    }

    if (fileName != null) {
        val glosa = Glosa(fileName)
        val controller = TextController(glosa, inverse)
        controller.go()
    } else {
        println("Usage: glosa <-i> [ordfil]")
        System.exit(1)
    }
}
