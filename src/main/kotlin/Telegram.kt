package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val BASE_URL = "https://api.telegram.org/bot"

fun main(argument: Array<String>) {
    val botToken = argument[0]
    var nextUpdateId = 0

    while (true) {
        Thread.sleep(1000)
        val updates = getUpdates(botToken, nextUpdateId)
        val startIndex = updates.lastIndexOf("update_id") + 11
        val endIndex = updates.lastIndexOf(",\n\"message\"")
        println(updates)
        if (startIndex != -1 && endIndex != -1) {
            val currentUpdateId = updates.substring(startIndex, endIndex)
            nextUpdateId = currentUpdateId.toInt() + 1
            println(updates)
        }
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlWithOffset = "$BASE_URL$botToken/getUpdates?offset=$updateId"
    val client = HttpClient.newBuilder().build()
    val requestGetUpdate = HttpRequest.newBuilder().uri(URI.create(urlWithOffset)).build()
    val responseGetUpdates = client.send(requestGetUpdate, HttpResponse.BodyHandlers.ofString())
    return responseGetUpdates.body()
}


