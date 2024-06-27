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

    return try {
        val word = parts[0]
        val translation = parts[1]
        val correctAnswersCount = parts[2].toIntOrNull() ?: 0
        Word(word, translation, correctAnswersCount)
    } catch (e: NumberFormatException) {
        null
    }
}

fun readWordsFromFile(fileName: String): MutableList<Word> {
    val dictionary = mutableListOf<Word>()
    File(fileName).readLines().forEach { it.toWord()?.let { line -> dictionary.add(line) } }
    return dictionary
}