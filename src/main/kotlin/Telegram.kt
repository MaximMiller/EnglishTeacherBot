package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateResponse(
    val ok: Boolean,
    val result: List<Update>,
)

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Int,
    val message: Message?,
)

@Serializable
data class Message(
    val chat: Chat,
    val text: String?,
)

@Serializable
data class Chat(
    val id: Int,
)

const val WELCOME = "hello"
const val START = "start"
const val BASE_URL = "https://api.telegram.org/bot"

fun main(argument: Array<String>) {
    val botToken = argument[0]
    val telegramBotService = TelegramBotService(botToken)
    var nextUpdateId = 0
    val json = telegramBotService.json

    while (true) {
        Thread.sleep(1000)
        val updatesJson: String = telegramBotService.getUpdates(nextUpdateId)
        println(updatesJson)

        val updates = updatesJson.let {
            json.decodeFromString<UpdateResponse>(it)
        }

        updates.result.forEach { update ->
            val updateId = update.updateId
            val chatId = update.message?.chat?.id
            val message = update.message?.text
            nextUpdateId = updateId + 1
            if (message != null && chatId != null) {
                when (message.lowercase()) {
                    WELCOME -> telegramBotService.sendMessage(chatId, "Hello")
                    START -> telegramBotService.sendMenu(chatId)
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