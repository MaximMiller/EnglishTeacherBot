package org.example

fun main() {
    fun List<Word>.getLearnedWordsCount() = count { it.correctAnswersCount >= 3 }

    fun List<Word>.getLearnedWordsPercentage() = if (isNotEmpty()) {
        getLearnedWordsCount() * 100 / size
    } else {
        0
    }

    fun printStatistics(words: List<Word>) {
        val learnedWordsCount = words.getLearnedWordsCount()
        val totalWordsCount = words.size
        val learnedPercentage = words.getLearnedWordsPercentage()

        println("Выучено $learnedWordsCount из $totalWordsCount слов | $learnedPercentage%")
    }
    while (true) {
        println(
            """
           Выберите пункт   
           Меню: 
           1 – Учить слова
           2 – Статистика
           0 – Выход
        """.trimIndent()
        )
        val answer = readln().toIntOrNull()
        when (answer) {
            1 -> println("Вы нажали на 1")
            2 -> {
                val words = readWordsFromFile("words.txt")
                printStatistics(words)
            }

            0 -> break
            else -> println("Ошибка! Введите число только из меню")
        }
    }
}