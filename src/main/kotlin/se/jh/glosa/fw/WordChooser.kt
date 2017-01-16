package se.jh.glosa.fw

import se.jh.glosa.vo.Word

interface WordChooser {

    fun nextIWord(inverse: Boolean): Word

    fun noToChooseAmong(): Int

}
