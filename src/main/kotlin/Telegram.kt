package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


fun main(argument: Array<String>) {
    val botToken = argument[0]
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates"

    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder().uri(URI.create(urlGetMe)).build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    println(response.body())

    val requestGetUpdates = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val responseGetUpdates = client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())
    println(responseGetUpdates.body())
}
