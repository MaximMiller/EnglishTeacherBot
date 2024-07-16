package org.example

import java.io.File

private const val MIN_AMOUNT_CORRECT_ANSWERS = 3

fun List<Word>.getLearnedWordsCount() = count { it.correctAnswersCount >= MIN_AMOUNT_CORRECT_ANSWERS }

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

fun List<Word>.getUnlearnedWords() = filter { it.correctAnswersCount < MIN_AMOUNT_CORRECT_ANSWERS }

fun saveDictionary(dictionary: List<Word>) {
    val file = File("words.txt")
    file.printWriter().use { out ->
        dictionary.forEach {
            out.println("${it.word},${it.translation},${it.correctAnswersCount}")
        }
    }
}

fun generateQuestionAndAnswers(words: List<Word>): Pair<Word, List<Word>>? {
    val unlearnedWords = words.getUnlearnedWords()
    if (unlearnedWords.isEmpty()) {
        println("Вы выучили все слова")
        return null
    }
    val questionWord = unlearnedWords.random()
    val answerOptions = (unlearnedWords - questionWord).shuffled().take(3) + questionWord
    return questionWord to answerOptions.shuffled()
}

fun main() {
    val words = readWordsFromFile("words.txt")
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
            1 -> {
                while (true) {
                    val (questionWord, answerOptions) = generateQuestionAndAnswers(words) ?: break

                    println("Слово для изучения: ${questionWord.word}")
                    println("Варианты ответа:")
                    answerOptions.forEachIndexed { index, word ->
                        println("${index + 1}. ${word.translation}")
                    }
                    println("0. Назад в меню")
                    val userInput = readln().toIntOrNull()

                    if (userInput == 0) {
                        println("Возвращение в главное меню...")
                        break
                    }

                    if (userInput != null && userInput in 1..4) {
                        val correctIndex = answerOptions.indexOfFirst { it.translation == questionWord.translation }
                        if (userInput - 1 == correctIndex) {
                            println("Правильно!")
                            questionWord.correctAnswersCount++
                            saveDictionary(words)
                        } else {
                            println("Неправильно. Правильный ответ: ${questionWord.translation}")
                        }
                    } else {
                        println("Неверный ввод. Пожалуйста, введите число от 0 до 4.")
                    }
                }
            }

            2 -> printStatistics(words)
            0 -> break
            else -> println("Ошибка! Введите число только из меню")
        }
    }
}
