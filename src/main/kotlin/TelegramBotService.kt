package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val BTN_LEARN_WORDS = "learn_words_clicked"
const val BTN_STATISTICS_CLICKED = "statistics_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

data class Word(
    val word: String,
    val translation: String,
    var correctAnswersCount: Int = 0,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Int,
    val text: String,
)

@Serializable
data class InlineKeyboardButton(
    val text: String,
    @SerialName("callback_data")
    val callbackData: String,
)

@Serializable
data class InlineKeyboardMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboardButton>>,
)

@Serializable
data class SendMenuRequest(
    @SerialName("chat_id")
    val chatId: Int,
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: InlineKeyboardMarkup,
)

class TelegramBotService(
    private val botToken: String,
    private val json: Json,
) {
    private val client = HttpClient.newBuilder().build()
    fun getUpdates(updateId: Int): String {
        val urlWithOffset = "$BASE_URL$botToken/getUpdates?offset=$updateId"
        val requestGetUpdate = HttpRequest.newBuilder().uri(URI.create(urlWithOffset)).build()
        val responseGetUpdates = client.send(requestGetUpdate, HttpResponse.BodyHandlers.ofString())
        return responseGetUpdates.body()
    }

    fun sendMessage(chatId: Int, message: String): String {
        val urlWithSendMessage = "$BASE_URL$botToken/sendMessage"
        val sendMessageRequest = SendMessageRequest(chatId, message)
        val bodySendMessage = json.encodeToString(sendMessageRequest)
        val requestSendMessage = HttpRequest.newBuilder()
            .uri(URI.create(urlWithSendMessage))
            .header("Content-Type", "application/json; charset=UTF-8")
            .POST(HttpRequest.BodyPublishers.ofString(bodySendMessage))
            .build()
        val responseSendMessage = client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
        return responseSendMessage.body()

    }

    fun sendMenu(chatId: Int): String {
        val urlWithSendMenu = "$BASE_URL$botToken/sendMessage"
        val inlineKeyboardButton = listOf(
            listOf(
                InlineKeyboardButton("Изучить слова", BTN_LEARN_WORDS),
                InlineKeyboardButton("Статистика", BTN_STATISTICS_CLICKED)
            )
        )
        val sendMenuRequest = SendMenuRequest(chatId, "Основное меню", InlineKeyboardMarkup(inlineKeyboardButton))
        val bodySendMenu = json.encodeToString(sendMenuRequest)
        val requestSendMenu = HttpRequest.newBuilder()
            .uri(URI.create(urlWithSendMenu))
            .header("Content-Type", "application/json; charset=UTF-8")
            .POST(HttpRequest.BodyPublishers.ofString(bodySendMenu))
            .build()
        val responseSendMenu = client.send(requestSendMenu, HttpResponse.BodyHandlers.ofString())
        return responseSendMenu.body()
    }

    fun sendQuestion(chatId: Int, question: Question): String {
        val correctAnswerText = question.word.translation
        val keyboardButtons = question.answerOptions.mapIndexed { index, answer ->
            InlineKeyboardButton(
                text = answer.translation,
                callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index"
            )
        }
        val inlineKeyboardMarkup = InlineKeyboardMarkup(
            inlineKeyboard = listOf(keyboardButtons)
        )
        val sendMenuRequest = SendMenuRequest(
            chatId = chatId,
            text = correctAnswerText,
            replyMarkup = inlineKeyboardMarkup
        )
        val jsonBody = Json.encodeToString(sendMenuRequest)
        val urlWithSendMessage = "$BASE_URL$botToken/sendMessage"
        val requestSendMessage = HttpRequest.newBuilder()
            .uri(URI.create(urlWithSendMessage))
            .header("Content-Type", "application/json; charset=UTF-8")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build()
        val responseSendMessage = client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
        return responseSendMessage.body()
    }

    fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, chatId: Int) {
        val question = trainer.getNextQuestion()
        if (question != null) {
            sendQuestion(chatId, question)
        } else {
            sendMessage(chatId, "Нет доступных вопросов.")
        }
    }
}

