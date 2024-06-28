package org.example

fun main() {
    val dictionary = readWordsFromFile("words.txt")
    dictionary.forEach { word ->
        println("Слово: ${word.word}, Перевод: ${word.translation}, Количество правильных ответов: " +
                "${word.correctAnswersCount}")
    }
}