package se.jh.glosa.vo

import se.jh.glosa.fw.WordFileReader
import se.jh.glosa.fw.WordFileReader.Companion.ALT_SEPARATOR

data class Word(val foreignWord: String, val localWord: String) : IWord {
    val foreignAlternatives = getAnswerAlternatives(foreignWord)

    val localAlternatives = getAnswerAlternatives(localWord)

    private var noOfUsedHist = 0

    private var noOfUsedSession = 0

    private var noOfCorrect = 0

    private var noOfUsedHistInverse = 0

    private var noOfUsedSessionInverse = 0

    private var noOfCorrectInverse = 0

    override fun getQuestion(inverse: Boolean): String {
        increaseUse(inverse)
        return if (!inverse) localWord else foreignWord
    }

    override fun getAnswer(inverse: Boolean): String = if (!inverse) foreignWord else localWord

    override fun isCorrect(answer: String, inverse: Boolean): Boolean {
        if (!inverse && foreignAlternatives.contains(answer.trim())) {
            increaseCorrect(inverse)
            return true
        }
        if (inverse && localAlternatives.contains(answer.trim())) {
            increaseCorrect(inverse)
            return true
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

    override fun getNoOfCorrect(inverse: Boolean) = if (!inverse) noOfCorrect else noOfCorrectInverse

    override fun getNoOfUsedHist(inverse: Boolean) = if (!inverse) noOfUsedHist else noOfUsedHistInverse

    override fun getNoOfUsedSession(inverse: Boolean) = if (!inverse) noOfUsedSession else noOfUsedSessionInverse

    private fun getAnswerAlternatives(answer: String): Set<String> {
        val returnSet = mutableSetOf(answer.trim())

        // Remove everything within parentheses (and the parentheses themselves)
        val cleanAnswer = answer.replace("\\(.*\\)".toRegex(), "")
        returnSet.add(cleanAnswer.trim())

        // Split on the ";", these constitute independent answers
        for (alternative in cleanAnswer.split(ALT_SEPARATOR)) {
            if (alternative.contains("[")) {
                // Remove brackets (but not what's inside the brackets)
                val optional = alternative.replace("[", "").replace("]", "")
                // Remove everything within brackets, as well as the brackets
                val mandatory = alternative.replace("\\[.*\\]".toRegex(), "")
                returnSet.add(mandatory.trim())
                returnSet.add(optional.trim())
            } else {
                returnSet.add(alternative.trim())
            }
        }
        return returnSet
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
