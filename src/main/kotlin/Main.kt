package org.example


fun main() {
    val trainer = LearnWordsTrainer()
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
        when (readln().toIntOrNull()) {
            1 -> {
                while (true) {
                    val question = trainer.getNextQuestion() ?: break

                    println("Слово для изучения: ${question.word.word}")
                    println("Варианты ответа:")
                    question.answerOptions.forEachIndexed { index, word ->
                        println("${index + 1}. ${word.translation}")
                    }
                    println("0. Назад в меню")
                    val userInput = readln().toIntOrNull()

                    if (userInput == 0) {
                        println("Возвращение в главное меню...")
                        break
                    }

                    trainer.checkAnswer(userInput, question)
                }
            }

            2 -> {
                val stats = trainer.getStatistics()
                println("Выучено ${stats.learnedWordsCount} из ${stats.totalWordsCount} слов | " +
                        "${stats.learnedPercentage}%")
            }

            0 -> break
            else -> println("Ошибка! Введите число только из меню")
        }
    }
}