package se.jh.glosa.vo

import java.io.PrintStream

interface IWord {
    fun provideHistoryLine(): String

    fun printQuestion(out: PrintStream, inverse: Boolean, width: Int)

    fun getQuestion(inverse: Boolean): String

    fun printAnswer(out: PrintStream, inverse: Boolean, width: Int)

    fun getAnswer(inverse: Boolean): String

    fun isCorrect(answer: String, inverse: Boolean): Boolean

    fun getNoOfUsedHist(inverse: Boolean): Int

    fun getNoOfUsedSession(inverse: Boolean): Int

    fun getNoOfCorrect(inverse: Boolean): Int

    fun initHistory(history: List<String>?)

}
