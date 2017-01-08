package se.jh.glosa.fw

import se.jh.glosa.txtui.TextController

class Glosa {
}

fun main(args: Array<String>) {
    println("Hello, world!")

    var inverse = false
    var oneShot = false
    var fileName: String? = null

    for (arg in args) {
        if (arg.equals("-i")) {
            inverse = true
        } else if (arg.equals("-o")) {
            oneShot = true
        } else {
            fileName = arg
        }
    }

    val glosa = Glosa()

    if (fileName != null) {
        val controller = TextController(glosa, fileName, inverse)
        controller.go(oneShot)
    } else {
        println("Usage: glosa <-i> <-o> [ordfil]")
        System.exit(1)
    }
}
