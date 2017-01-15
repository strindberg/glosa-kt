package se.jh.glosa.fw

import se.jh.glosa.vo.IWord
import se.jh.glosa.vo.Word
import java.io.*
import java.util.*

class WordFileReader(private val fileName: String) {

    val words: List<IWord>

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
            BufferedReader(FileReader(historyFile)).use { historyIn ->
                historyIn.readLine() // History version line
                var inLine = historyIn.readLine()
                while (inLine != null) {
                    val lineParts = inLine.split(HISTORY_SEPARATOR).toTypedArray()
                    historyMap.put(lineParts[0].trim(), lineParts.slice(1.until(lineParts.size)))
                    inLine = historyIn.readLine()
                }
            }
        }
        return historyMap
    }

    private fun readWords(historyMap: Map<String, List<String>>): List<IWord> {
        val returnWords = ArrayList<IWord>()
        BufferedReader(FileReader(fileName)).use { reader ->
            var readLine = reader.readLine()
            var lineCount = 1
            while (readLine != null) {
                if (!readLine.startsWith(COMMENT_CHAR) && readLine.isNotEmpty()) {
                    if (!readLine.contains(SEPARATOR)) {
                        throw IOException("Wrong format in file, line $lineCount: $readLine")
                    }

                    val words = readLine.split(SEPARATOR)
                    val word = Word(words[0].trim(), words[1].trim())

                    word.initHistory(historyMap.get(readLine.trim()))
                    returnWords.add(word)
                }
                readLine = reader.readLine()
                lineCount++
            }
        }
        return returnWords
    }

    fun saveHistory(words: List<IWord>) {
        BufferedWriter(FileWriter(historyFileName())).use { historyWriter ->
            historyWriter.write("${COMMENT_CHAR}${HISTORY_FILE_VERSION} \n")
            words.forEach { historyWriter.write(it.provideHistoryLine()) }
        }
    }

    companion object {
        val WORD_FILE_SUFFIX = ".txt"

        val HISTORY_FILE_SUFFIX = ".his"

        val HISTORY_SEPARATOR = "^"

        val COMMENT_CHAR = "#"

        val HISTORY_FILE_VERSION = 2

        val SEPARATOR = "|"

        val ALT_SEPARATOR = ";"
    }

}
