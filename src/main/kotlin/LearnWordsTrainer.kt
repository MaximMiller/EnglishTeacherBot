package org.example

import java.io.File

private const val AMOUNT_PARTS_IN_LINE = 3

data class Word(
    val word: String,
    val translation: String,
    var correctAnswersCount: Int = 0,
)

data class Statistics(
    val learnedWordsCount: Int,
    val totalWordsCount: Int,
    val learnedPercentage: Int,
)

data class Question(
    val word: Word,
    val answerOptions: List<Word>,
)

class LearnWordsTrainer(
    private val numberOptionsShownQuestion: Int,
    private val minAmountCorrectAnswers: Int,
    private val fileName:String,
) {
    private val words: List<Word> = loadDictionary()

    fun getStatistics(): Statistics {
        val learnedWordsCount = words.count { it.correctAnswersCount >= minAmountCorrectAnswers }
        val totalWordsCount = words.size
        val learnedPercentage = if (totalWordsCount > 0) {
            learnedWordsCount * 100 / totalWordsCount
        } else {
            0
        }
        return Statistics(learnedWordsCount, totalWordsCount, learnedPercentage)
    }

    fun getQuestion(): Question? {
        val unlearnedWords = words.filter { it.correctAnswersCount < minAmountCorrectAnswers }
        if (unlearnedWords.isEmpty()) {
            return null
        }
        val questionWord = unlearnedWords.random()
        val answerOptions = (unlearnedWords - questionWord).shuffled().take(numberOptionsShownQuestion) + questionWord
        return Question(questionWord, answerOptions.shuffled())

    }

    fun getNextQuestion(): Question? {
        return getQuestion()
    }

    fun checkAnswer(userAnswerIndex: Int?, question: Question): Boolean {
        if (userAnswerIndex == null || userAnswerIndex !in 1..numberOptionsShownQuestion) {
            return false
        }
        val correctIndex = question.answerOptions.indexOfFirst { it.translation == question.word.translation }
        return if (userAnswerIndex - 1 == correctIndex) {
            question.word.correctAnswersCount++
            saveDictionary()
            true
        } else {
            false
        }
    }

    private fun loadDictionary(): List<Word> {
        return File(fileName).readLines().mapNotNull { it.toWord() }
    }

    private fun saveDictionary() {
        File(fileName).bufferedWriter().use { out ->
            words.forEach {
                out.write("${it.word}|${it.translation}|${it.correctAnswersCount}\n")
            }
        }
    }

    private fun String.toWord(): Word? {
        val parts = this.split("|")
        if (parts.size < AMOUNT_PARTS_IN_LINE) return null

        val word = parts[0]
        val translation = parts[1]
        val correctAnswersCount = parts[2].toIntOrNull() ?: 0
        return Word(word, translation, correctAnswersCount)
    }
}
