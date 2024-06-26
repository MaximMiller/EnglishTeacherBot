package org.example

import java.io.File

fun main() {
    val fileWords: File = File("words.txt")
    for (i in fileWords.readLines()) {
        println(i)
    }
}
/*
Воспользоваться методом readLines(), чтобы достать строки из файла.
Распечатать их в цикле (каждая с новой строки соответственно), чтобы удостовериться, что все работает.
 */