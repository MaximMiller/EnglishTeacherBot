package org.example

fun main() {
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
            2 -> println("Вы нажали на 2")
            0 -> break
            else -> println("Ошибка! Введите число только из меню")
        }
    }
}