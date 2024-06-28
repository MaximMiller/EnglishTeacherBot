package org.example

import java.io.File

private const val AMOUNT_PARTS_IN_LINE = 3

data class Word(
    val word: String,
    val translation: String,
    var correctAnswersCount: Int = 0,
)

fun String.toWord(): Word? {
    val parts = this.split("|")
    if (parts.size < AMOUNT_PARTS_IN_LINE) return null

    val word = parts[0]
    val translation = parts[1]
    val correctAnswersCount = parts[2].toIntOrNull() ?: 0
    return Word(word, translation, correctAnswersCount)
}

fun readWordsFromFile(fileName: String): List<Word> {
    return File(fileName).readLines().mapNotNull { it.toWord() }
}