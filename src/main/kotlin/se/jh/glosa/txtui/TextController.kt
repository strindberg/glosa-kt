package se.jh.glosa.txtui

import se.jh.glosa.fw.Glosa
import java.io.File
import java.io.IOException
import java.io.InputStream

class TextController(glosa: Glosa, fileName: String, inverse: Boolean) {

    init {
        try {
            glosa.newFile(File(fileName))
        } catch (ioe: IOException) {
            ioe.printStackTrace()
            System.exit(1)
        }

    }

    fun go(oneShot: Boolean): Unit { }

}
