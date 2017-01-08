package se.jh.glosa.vo

import java.io.PrintStream
import java.io.IOException
import java.io.BufferedReader
import java.io.Writer


interface IWord {
    @Throws(IOException::class)
    fun writeToHistoryWriter(writer: Writer)

    @Throws(IOException::class)
    fun readFromReader(reader: BufferedReader, lineCount: Int, historyMap: Map<String, IWordHistory>): Int

    fun printQuestion(out: PrintStream, inverse: Boolean): Int

    fun getQuestion(inverse: Boolean): String

    fun printAnswer(out: PrintStream, inverse: Boolean): Int

    fun getAnswer(inverse: Boolean): String

    fun isCorrect(answer: String, inverse: Boolean): Boolean

    fun getNoOfUsed(inverse: Boolean): Int

    fun getNoOfCorrect(inverse: Boolean): Int

}
