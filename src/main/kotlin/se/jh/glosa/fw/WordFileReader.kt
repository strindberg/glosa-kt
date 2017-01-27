package se.jh.glosa.fw

import se.jh.glosa.vo.DefaultWord
import se.jh.glosa.vo.Word
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

const val WORD_FILE_SUFFIX = ".txt"
const val HISTORY_FILE_SUFFIX = ".his"
const val HISTORY_SEPARATOR = "^"
const val COMMENT_CHAR = "#"
const val HISTORY_FILE_VERSION = 2
const val SEPARATOR = "|"
const val ALT_SEPARATOR = ";"

class WordFileReader(private val fileName: String) {

    val words: List<Word>

    init {
        if (!fileName.endsWith(WORD_FILE_SUFFIX)) {
            throw IOException("Input file must end with $WORD_FILE_SUFFIX.")
        }
        val historyMap = fillHistoryMap(historyFileName())
        words = readWords(historyMap)
    }

    private fun historyFileName() = fileName.substring(0, fileName.lastIndexOf(".")) + HISTORY_FILE_SUFFIX

    private fun fillHistoryMap(historyFileName: String): Map<String, List<String>> {
        val historyMap: MutableMap<String, List<String>> = HashMap()
        val historyFile = File(historyFileName)
        if (historyFile.exists()) {
            historyFile.forEachLine { line ->
                if (!line.startsWith(COMMENT_CHAR) && line.isNotEmpty()) {
                    val lineParts = line.split(HISTORY_SEPARATOR)
                    historyMap.put(lineParts[0].trim(), lineParts.takeLast(lineParts.size - 1))
                }
            }
        }
        return historyMap
    }

    private fun readWords(historyMap: Map<String, List<String>>): List<Word> {
        val returnWords = ArrayList<Word>()
        File(fileName).forEachLine { line ->
            if (!line.startsWith(COMMENT_CHAR) && line.isNotEmpty()) {
                if (!line.contains(SEPARATOR)) {
                    throw IOException("Wrong format in file, line: $line")
                }

                val words = line.split(SEPARATOR)
                val word = DefaultWord(words[0].trim(), words[1].trim())

                word.initHistory(historyMap.get(line.trim()))
                returnWords.add(word)
            }
        }
        return returnWords
    }

    fun saveHistory(words: List<Word>) {
        BufferedWriter(FileWriter(historyFileName())).use { historyWriter ->
            historyWriter.write("${COMMENT_CHAR}${HISTORY_FILE_VERSION} \n")
            words.forEach { historyWriter.write(it.provideHistoryLine()) }
        }
    }

}
