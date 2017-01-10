package se.jh.glosa.fw

import se.jh.glosa.vo.IWord

interface IWordChooser {

    fun nextIWord(inverse: Boolean): IWord

    fun noToChooseAmong(): Int

}
