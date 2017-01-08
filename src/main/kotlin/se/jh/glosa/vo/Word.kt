package se.jh.glosa.vo

import java.io.BufferedReader
import java.io.IOException
import java.io.PrintStream
import java.io.Writer
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

import se.jh.glosa.fw.WordFileReader

class Word : IWord {

    private var foreignWord: String? = null

    private var swedishWord: String? = null

    private var foreignAlternatives: List<String>? = null

    private var swedishAlternatives: List<String>? = null

    private var noOfUsed = 0

    private var noOfCorrect = 0

    private var noOfUsedInverse = 0

    private var noOfCorrectInverse = 0

    override fun printQuestion(out: PrintStream, inverse: Boolean): Int {
        increaseUse(inverse)
        val length: Int
        if (!inverse) {
            out.print(swedishWord!!.trim { it <= ' ' })
            length = swedishWord!!.trim { it <= ' ' }.length
        } else {
            out.print(foreignWord!!.trim { it <= ' ' })
            length = foreignWord!!.trim { it <= ' ' }.length
        }
        return length
    }

    override fun getQuestion(inverse: Boolean): String {
        increaseUse(inverse)
        if (!inverse) {
            return swedishWord!!.trim { it <= ' ' }
        } else {
            return foreignWord!!.trim { it <= ' ' }
        }
    }

    override fun printAnswer(out: PrintStream, inverse: Boolean): Int {
        val length: Int
        if (!inverse) {
            out.print(foreignWord!!.trim { it <= ' ' })
            length = foreignWord!!.trim { it <= ' ' }.length
        } else {
            out.print(swedishWord!!.trim { it <= ' ' })
            length = swedishWord!!.trim { it <= ' ' }.length
        }
        return length
    }

    override fun getAnswer(inverse: Boolean): String {
        if (!inverse) {
            return foreignWord!!.trim { it <= ' ' }
        } else {
            return swedishWord!!.trim { it <= ' ' }
        }
    }

    override fun isCorrect(answer: String, inverse: Boolean): Boolean {
        var isCorrect = false
        if (!inverse) {
            isCorrect = answer != null && foreignAlternatives!!.contains(answer.trim { it <= ' ' })
            if (isCorrect) {
                increaseCorrect(inverse)
            }
        } else {
            isCorrect = answer != null && swedishAlternatives!!.contains(answer.trim { it <= ' ' })
            if (isCorrect) {
                increaseCorrect(inverse)
            }
        }
        return isCorrect
    }

    private fun getAnswerAlternatives(answer: String): List<String> {
        val returnList = ArrayList<String>()
        returnList.add(answer)
        // Remove everything within parentheses (and the parentheses themselves)
        val cleanAnswer = answer.replace("\\(.*\\)".toRegex(), "")
        returnList.add(cleanAnswer)
        // Split on the ";", these constitute independent answers
        val alternatives = cleanAnswer.split(Pattern.quote(ALT_SEPARATOR).toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in alternatives.indices) {
            val alternative = alternatives[i]
            if (alternative.contains("[")) {
                // Remove brackets (but not what's inside the brackets)
                val optional = alternative.replace("\\[".toRegex(), "").replace("\\]".toRegex(), "")
                val p = Pattern.compile("\\[.*\\]")
                val m = p.matcher(alternative)
                // Remove everything within brackets, as well as the brackets
                val mandatory = m.replaceAll("")
                returnList.add(mandatory.trim { it <= ' ' })
                returnList.add(optional.trim { it <= ' ' })
            } else {
                returnList.add(alternative.trim { it <= ' ' })
            }
        }
        return returnList
    }

    private fun increaseUse(inverse: Boolean) {
        if (!inverse) {
            noOfUsed++
        } else {
            noOfUsedInverse++
        }
    }

    private fun increaseCorrect(inverse: Boolean) {
        if (!inverse) {
            noOfCorrect++
        } else {
            noOfCorrectInverse++
        }
    }

    @Throws(IOException::class)
    override fun readFromReader(reader: BufferedReader, lineCount: Int, historyMap: Map<String, IWordHistory>): Int {
        val readObject = readValidLine(reader)
        val readLine = readObject.readLine
        var linesRead = readObject.noOfLines
        if (readLine != null) {
            if (!readLine.contains(SEPARATOR)) {
                throw IOException("Wrong format in file, line " + (lineCount + linesRead) + ": " + readLine)
            }
            val history = historyMap[readLine]
            foreignWord = readLine.split(Pattern.quote(SEPARATOR).toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            swedishWord = readLine.split(Pattern.quote(SEPARATOR).toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            foreignAlternatives = getAnswerAlternatives(foreignWord)
            swedishAlternatives = getAnswerAlternatives(swedishWord)
            if (history != null) {
                noOfUsed = history.first.toInt()
                noOfCorrect = history.second.toInt()
                noOfUsedInverse = history.third.toInt()
                noOfCorrectInverse = history.fourth.toInt()
            }
        } else {
            // If we don't have a valid line, the readValidLine has reached end of file or has only
            // read comment lines.
            linesRead = 0
        }
        return linesRead
    }

    @Throws(IOException::class)
    private fun readValidLine(reader: BufferedReader): ReadObject {
        var readLine: String? = null
        var linesRead = 0
        var goOn = true
        while (goOn) {
            readLine = reader.readLine()
            if (readLine == null) {
                goOn = false
            } else {
                linesRead++
                // Ignore comment lines starting with comment char
                if (!readLine.startsWith(COMMENT_CHAR) && readLine.length != 0) {
                    goOn = false
                }
            }
        }
        return ReadObject(readLine, linesRead)
    }

    @Throws(IOException::class)
    override fun writeToHistoryWriter(historyWriter: Writer) {
        historyWriter.write(foreignWord + SEPARATOR + swedishWord + WordFileReader.HISTORY_SEPARATOR + noOfUsed
                + WordFileReader.HISTORY_SEPARATOR + noOfCorrect + WordFileReader.HISTORY_SEPARATOR + noOfUsedInverse
                + WordFileReader.HISTORY_SEPARATOR + noOfCorrectInverse + "\n")
    }

    override fun getNoOfCorrect(inverse: Boolean): Int {
        if (!inverse) {
            return noOfCorrect
        } else {
            return noOfCorrectInverse
        }
    }

    override fun getNoOfUsed(inverse: Boolean): Int {
        if (!inverse) {
            return noOfUsed
        } else {
            return noOfUsedInverse
        }
    }

    override fun equals(`object`: Any?): Boolean {
        if (`object` !is Word) {
            return false
        }
        return `object`.swedishWord != null && swedishWord != null && `object`.swedishWord == swedishWord
                && `object`.foreignWord != null && foreignWord != null
                && `object`.foreignWord == foreignWord
    }

    override fun hashCode(): Int {
        var returnHash = 17
        returnHash = returnHash + 37 * foreignWord!!.hashCode()
        returnHash = returnHash + 37 * swedishWord!!.hashCode()
        return returnHash
    }

    private inner class ReadObject(val readLine: String, val noOfLines: Int)

    companion object {

        private val SEPARATOR = "|"

        private val COMMENT_CHAR = "#"

        private val ALT_SEPARATOR = ";"
    }
}
