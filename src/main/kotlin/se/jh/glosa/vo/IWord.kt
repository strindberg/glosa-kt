package se.jh.glosa.vo

interface IWord {
    fun provideHistoryLine(): String

    fun getQuestion(inverse: Boolean): String

    fun getAnswer(inverse: Boolean): String

    fun isCorrect(answer: String, inverse: Boolean): Boolean

    fun getNoOfUsedHist(inverse: Boolean): Int

    fun getNoOfUsedSession(inverse: Boolean): Int

    fun getNoOfCorrect(inverse: Boolean): Int

    fun initHistory(history: List<String>?)

}
