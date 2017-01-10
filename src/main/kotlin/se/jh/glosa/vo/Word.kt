package se.jh.glosa.vo

import se.jh.glosa.fw.WordFileReader
import java.io.PrintStream
import java.util.*
import java.util.regex.Pattern

data class Word(val foreignWord: String, val localWord: String) : IWord {
    companion object {
        private val ALT_SEPARATOR = ";"
    }

    private val foreignAlternatives: List<String>

    private val localAlternatives: List<String>

    private var noOfUsedHist = 0

    private var noOfUsedSession = 0

    private var noOfCorrect = 0

    private var noOfUsedHistInverse = 0

    private var noOfUsedSessionInverse = 0

    private var noOfCorrectInverse = 0

    init {
        foreignAlternatives = getAnswerAlternatives(foreignWord)
        localAlternatives = getAnswerAlternatives(localWord)
    }

    override fun printQuestion(out: PrintStream, inverse: Boolean): Int {
        increaseUse(inverse)
        if (!inverse) {
            out.print(localWord)
            return localWord.length
        } else {
            out.print(foreignWord)
            return foreignWord.length
        }
    }

    override fun getQuestion(inverse: Boolean): String {
        increaseUse(inverse)
        return if (!inverse) localWord else foreignWord
    }

    override fun printAnswer(out: PrintStream, inverse: Boolean): Int {
        if (!inverse) {
            out.print(foreignWord)
            return foreignWord.length
        } else {
            out.print(localWord)
            return localWord.length
        }
    }

    override fun getAnswer(inverse: Boolean): String = if (!inverse) foreignWord else localWord

    override fun isCorrect(answer: String, inverse: Boolean): Boolean {
        if (!inverse) {
            if (foreignAlternatives.contains(answer.trim())) {
                increaseCorrect(inverse)
                return true
            }
        } else {
            if (localAlternatives.contains(answer.trim())) {
                increaseCorrect(inverse)
                return true
            }
        }
        return false
    }

    override fun provideHistoryLine(): String {
        return foreignWord + " " + WordFileReader.SEPARATOR + " " + localWord + " " + WordFileReader.HISTORY_SEPARATOR + noOfUsedHist +
                WordFileReader.HISTORY_SEPARATOR + noOfCorrect + WordFileReader.HISTORY_SEPARATOR + noOfUsedHistInverse +
                WordFileReader.HISTORY_SEPARATOR + noOfCorrectInverse + "\n"
    }

    override fun initHistory(history: List<String>?) {
        if (history != null && history.size == 4) {
            noOfUsedHist = history[0].toInt()
            noOfCorrect = history[1].toInt()
            noOfUsedHistInverse = history[2].toInt()
            noOfCorrectInverse = history[3].toInt()
        }
    }

    override fun getNoOfCorrect(inverse: Boolean): Int {
        if (!inverse) {
            return noOfCorrect
        } else {
            return noOfCorrectInverse
        }
    }

    override fun getNoOfUsedHist(inverse: Boolean): Int {
        if (!inverse) {
            return noOfUsedHist
        } else {
            return noOfUsedHistInverse
        }
    }

    override fun getNoOfUsedSession(inverse: Boolean): Int {
        if (!inverse) {
            return noOfUsedSession
        } else {
            return noOfUsedSessionInverse
        }
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
                // TODO: check this logic
                // Remove brackets (but not what's inside the brackets)
                val optional = alternative.replace("\\[".toRegex(), "").replace("\\]".toRegex(), "")
                val p = Pattern.compile("\\[.*\\]")
                val m = p.matcher(alternative)
                // Remove everything within brackets, as well as the brackets
                val mandatory = m.replaceAll("")
                returnList.add(mandatory.trim())
                returnList.add(optional.trim())
            } else {
                returnList.add(alternative.trim())
            }
        }
        return returnList
    }

    private fun increaseUse(inverse: Boolean) {
        if (!inverse) {
            noOfUsedHist++
            noOfUsedSession++
        } else {
            noOfUsedHistInverse++
            noOfUsedSessionInverse++
        }
    }

    private fun increaseCorrect(inverse: Boolean) {
        if (!inverse) {
            noOfCorrect++
        } else {
            noOfCorrectInverse++
        }
    }

}
