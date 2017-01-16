package se.jh.glosa.vo

interface Word {
    fun getQuestion(inverse: Boolean): String

    fun getAnswer(inverse: Boolean): String

    fun isCorrect(answer: String, inverse: Boolean): Boolean

    fun getNoOfUsedHist(inverse: Boolean): Int

    fun getNoOfUsedSession(inverse: Boolean): Int

    fun getNoOfCorrect(inverse: Boolean): Int

    fun initHistory(history: List<String>?)

    fun provideHistoryLine(): String
}
