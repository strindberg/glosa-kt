package se.jh.glosa.vo

import io.kotlintest.specs.FunSpec

class DefaultWordTest : FunSpec() {
    init {
        test("Parenthesis should be ignored") {
            val word = DefaultWord(" palabra (extra) ", "ord")
            word.isCorrect("palabra (extra)", false) shouldBe true
            word.isCorrect("palabra", false) shouldBe true
        }

        test("Semi-colon signifies alternatives") {
            val word = DefaultWord("lanzar ", "utsända; avfyra ")
            word.isCorrect("utsända", true) shouldBe true
            word.isCorrect("avfyra", true) shouldBe true
        }

        test("Brackets signify optional chars") {
            val word = DefaultWord(" la cría", " [djur]ungen")
            word.isCorrect("djurungen", true) shouldBe true
            word.isCorrect("ungen", true) shouldBe true
        }
    }
}
