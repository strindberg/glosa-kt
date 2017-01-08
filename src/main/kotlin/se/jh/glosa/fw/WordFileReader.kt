package se.jh.glosa.fw

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import java.util.regex.Pattern

import se.jh.glosa.vo.IWord
import se.jh.glosa.vo.IWordHistory
import se.jh.glosa.vo.Word

class WordFileReader @Throws(IOException::class)
constructor(private val fileName: String) {

    private var historyFileVersion: Int = 0

    private val historyFileName: String

    internal var historyMap: MutableMap<String, IWordHistory>

    init {
        if (!fileName.endsWith(WORD_FILE_SUFFIX)) {
            throw IOException("Input file must end with $WORD_FILE_SUFFIX.")
        }
        val indexOfDot = fileName.lastIndexOf(".")
        historyFileName = fileName.substring(0, indexOfDot) + HISTORY_FILE_SUFFIX
    }

    val words: List<IWord>
        @Throws(IOException::class)
        get() {
            val `in` = BufferedReader(FileReader(fileName))
            val returnWords = ArrayList<IWord>()
            fillHistoryMap()
            var goOn = true
            var readLines = 0
            while (goOn) {
                val word = Word()
                val lineResult = word.readFromReader(`in`, readLines, historyMap)
                if (lineResult > 0) {
                    readLines += lineResult
                    returnWords.add(word)
                } else {
                    goOn = false
                }
            }
            `in`.close()
            return returnWords
        }

    @Throws(IOException::class)
    private fun fillHistoryMap() {
        historyMap = HashMap<String, IWordHistory>()
        val historyFile = File(historyFileName)
        if (historyFile.exists()) {
            val historyIn = BufferedReader(FileReader(historyFile))
            var inLine: String? = historyIn.readLine()
            try {
                historyFileVersion = Integer.parseInt(inLine!!.substring(1, inLine.indexOf(" ")))
            } catch (e: Exception) {
                throw IOException("History file must begin with version number!")
            }

            var goOn = true
            while (goOn) {
                inLine = historyIn.readLine()
                if (inLine == null) {
                    goOn = false
                } else {
                    val lineParts = inLine.split(Pattern.quote(HISTORY_SEPARATOR).toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val wordLine = lineParts[0]
                    val noOfUsed = Integer.parseInt(lineParts[1])
                    val noOfCorrect = Integer.parseInt(lineParts[2])
                    val noOfUsedInverse = Integer.parseInt(lineParts[3])
                    val noOfCorrectInverse = Integer.parseInt(lineParts[4])
                    val history = WordHistory(noOfUsed, noOfCorrect, noOfUsedInverse, noOfCorrectInverse)
                    historyMap.put(wordLine, history)
                }
            }
            historyIn.close()
        }
    }

    @Throws(IOException::class)
    fun saveHistory(words: List<IWord>) {
        val historyWriter = BufferedWriter(FileWriter(historyFileName))
        historyWriter.write(COMMENT_CHAR + HISTORY_FILE_VERSION + " \n")
        for (word in words) {
            word.writeToHistoryWriter(historyWriter)
        }
        historyWriter.close()
    }

    private class WordHistory(private val noOfUsed: Int, private val noOfCorrect: Int, private val noOfUsedInverse: Int, private val noOfCorrectInverse: Int) : IWordHistory {

        override val first: Number
            get() = noOfUsed

        override val second: Number
            get() = noOfCorrect

        override val third: Number
            get() = noOfUsedInverse

        override val fourth: Number
            get() = noOfCorrectInverse

    }

    companion object {
        val WORD_FILE_SUFFIX = ".txt"

        val HISTORY_FILE_SUFFIX = ".his"

        val HISTORY_SEPARATOR = "^"

        private val COMMENT_CHAR = "#"

        private val HISTORY_FILE_VERSION = 2
    }

}
