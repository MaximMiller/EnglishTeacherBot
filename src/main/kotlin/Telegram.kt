package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UpdateResponse(
    val ok: Boolean,
    val result: List<Update>,
)

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Int,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
    val message: Message? = null,
)

@Serializable
data class CallbackQuery(
    val id: String,
    val message: Message,
    @SerialName("chat_instance")
    val chatInstance: String,
    val data: String,
)

@Serializable
data class Message(
    val chat: Chat,
    val date: Long,
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboardButton>>,
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
    val json = Json { ignoreUnknownKeys = true }
    val telegramBotService = TelegramBotService(botToken, json)
    val learnWordsTrainer = LearnWordsTrainer(4, 3, "words.txt")
    var nextUpdateId = 0

    while (true) {
        Thread.sleep(1000)
        val updatesJson: String = telegramBotService.getUpdates(nextUpdateId)
        println(updatesJson)

        val updates = updatesJson.let {
            json.decodeFromString<UpdateResponse>(it)
        }

        updates.result.forEach { update ->
            val updateId = update.updateId
            nextUpdateId = updateId + 1
            update.message?.let { message ->
                val chatId = message.chat.id
                val text = message.text
                if (text != null) {
                    when (text.lowercase()) {
                        WELCOME -> telegramBotService.sendMessage(chatId, "Hello")
                        START -> telegramBotService.sendMenu(chatId)
                    }
                }
            }
            update.callbackQuery?.let { callbackQuery ->
                val chatId = callbackQuery.message?.chat?.id
                val data = callbackQuery.data
                if (chatId != null) {
                    when (data) {
                        BTN_LEARN_WORDS -> {
                            telegramBotService.checkNextQuestionAndSend(learnWordsTrainer, chatId)
                        }

                        BTN_STATISTICS_CLICKED -> {
                            val statistics = learnWordsTrainer.getStatistics()
                            val formattedStatistics = learnWordsTrainer.formatStatistics(statistics)
                            telegramBotService.sendMessage(chatId, formattedStatistics)
                        }
                    }
                }
            }
        }
    }
}
