package se.jh.glosa.fw

import se.jh.glosa.vo.IWord
import java.util.*

class SuccessiveRandomWordChooser(private val words: List<IWord>) : IWordChooser {

    private val random = Random()

    private var previousWord: IWord? = null

    private var noToChooseAmong: Int = 0

    override fun nextIWord(inverse: Boolean): IWord {
        //Choose the word(s) which have been given correctly the least number of times
        val minimumCorrect = words.minBy { it.getNoOfCorrect(inverse) }
        val correctWords = words.filter { it.getNoOfCorrect(inverse) == minimumCorrect?.getNoOfCorrect(inverse) }

        noToChooseAmong = correctWords.size

        // Among the words with the same number of correct answers, choose the one(s) which have
        // been shown (in this run) the least number of times.
        val minimumShown = correctWords.minBy { it.getNoOfUsedSession(inverse) }
        val shownWords = correctWords.filter { it.getNoOfUsedSession(inverse) == minimumShown?.getNoOfUsedSession(inverse) }

        // Among the words with the same number of times shown (in this run), choose the one(s) which have
        // been shown historically the least number of times.
        val minimumHistory = shownWords.minBy { it.getNoOfUsedHist(inverse) }
        var historyShownWords = shownWords.filter { it.getNoOfUsedHist(inverse) == minimumHistory?.getNoOfUsedHist(inverse) }

        // Make sure we never show the same word twice in a row, unless it's the last word
        if (previousWord != null && historyShownWords.size > 1) {
            historyShownWords = historyShownWords.filter { it != previousWord }
        }

        // Randomly choose one word from the list of candidates
        val returnWord = historyShownWords[random.nextInt(historyShownWords.size)]

        previousWord = returnWord
        return returnWord
    }

    override fun noToChooseAmong(): Int {
        return noToChooseAmong
    }

}
