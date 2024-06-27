package org.example

import java.io.File

fun main() {
    val fileWords: File = File("words.txt")
    for (i in fileWords.readLines()) {
        println(i)
    }
}