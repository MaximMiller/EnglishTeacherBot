package org.example

import java.io.File

fun main() {
    val fileWords: File = File("words.txt")
//    fileWords.createNewFile()
//    fileWords.readLines()

    fileWords.forEachLine { println(it) }
}