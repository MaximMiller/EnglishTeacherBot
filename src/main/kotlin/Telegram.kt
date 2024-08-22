package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val BASE_URL = "https://api.telegram.org/bot"

fun main(argument: Array<String>) {
    val botToken = argument[0]
    var lastUpdateId = 0
    val telegramBotService = TelegramBotService(botToken)
    val json = Json { ignoreUnknownKeys = true }

    while (true) {
        Thread.sleep(1000)

        val updatesJson: String? = telegramBotService.getUpdates(lastUpdateId)
        println(updatesJson)

        val updates = updatesJson?.let {
            json.decodeFromString<UpdateResponse>(it)
        } ?: continue

        updates.result.forEach { update ->
            val updateId = update.update_id
            val chatId = update.message?.chat?.id
            val message = update.message?.text
            lastUpdateId = updateId + 1

            if (message != null && chatId != null) {
                when (message.lowercase()) {
                    "hello" -> telegramBotService.sendMessage(chatId, "Hello")
                    "start" -> telegramBotService.sendMenu(chatId)
                }
            }
        }
    }
}

@Serializable
data class UpdateResponse(val ok: Boolean, val result: List<Update>)

@Serializable
data class Update(val update_id: Int, val message: Message?)

@Serializable
data class Message(val chat: Chat, val text: String?)

@Serializable
data class Chat(val id: Int)