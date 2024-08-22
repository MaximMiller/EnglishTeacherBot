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
            val chatId = getChatId(update)
            val message = update.message?.text
            val callbackData = update.callback_query?.data

            lastUpdateId = updateId + 1
            println(lastUpdateId)

            if (message != null && chatId != null) {
                when (message.lowercase()) {
                    "hello" -> telegramBotService.sendMessage(chatId, "Hello")
                    "start" -> telegramBotService.sendMenu(chatId)
                }
            }

            if (callbackData != null && chatId != null) {
                when (callbackData.lowercase()) {
                    "statistics_clicked" -> telegramBotService.sendMessage(chatId, "Выучено 10 из 10 слов | 100%")
                    "learn_words_clicked" -> telegramBotService.sendMessage(chatId, "words")
                }
            }
        }
    }
}

@Serializable
data class UpdateResponse(
    val ok: Boolean,
    val result: List<Update>,
)

@Serializable
data class Update(
    val update_id: Int,
    val message: Message? = null,
    val callback_query: CallbackQuery? = null,
)

@Serializable
data class Message(
    val chat: Chat,
    val text: String?,
)

@Serializable
data class CallbackQuery(
    val id: String,
    val from: User,
    val message: Message?,
    val data: String,
)

@Serializable
data class User(
    val id: Int,
)

@Serializable
data class Chat(
    val id: Int,
)

fun getChatId(update: Update): Int? {
    return update.message?.chat?.id ?: update.callback_query?.message?.chat?.id
}