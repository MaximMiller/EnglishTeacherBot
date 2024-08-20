package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService {
    fun getUpdates(botToken: String, updateId: Int): String {
        val urlWithOffset = "$BASE_URL$botToken/getUpdates?offset=$updateId"
        val client = HttpClient.newBuilder().build()
        val requestGetUpdate = HttpRequest.newBuilder().uri(URI.create(urlWithOffset)).build()
        val responseGetUpdates = client.send(requestGetUpdate, HttpResponse.BodyHandlers.ofString())
        return responseGetUpdates.body()
    }

    fun sendMessage(botToken: String, chatId: Int, message: String): String {
        val urlWithSendMessage = "$BASE_URL$botToken/sendMessage"
        val bodySendMessage = """
            {
                "chat_id": $chatId,
                "text": "$message"
            }
        """.trimIndent()

        val client = HttpClient.newBuilder().build()
        val requestSendMessage = HttpRequest.newBuilder()
            .uri(URI.create(urlWithSendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(bodySendMessage))
            .build()
        val responseSendMessage = client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
        return responseSendMessage.body()
    }
}
