package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

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
) {
    val json = Json { ignoreUnknownKeys = true }
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
        return try {
            client.newCall(requestSendMessage).execute().use { responseSendMessage ->
                if (responseSendMessage.isSuccessful) {
                    responseSendMessage.body?.string()
                } else {
                    val errorMsg = "Failed to send message: ${responseSendMessage.code} ${responseSendMessage.message}"
                    throw IOException(errorMsg)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

    }

    fun sendMenu(chatId: Int): String? {
        val urlWithSendMenu = "$BASE_URL$botToken/sendMessage"
        val inlineKeyboard = listOf(
            listOf(
                InlineKeyboardButton("Изучить слова", "learn_words_clicked"),
                InlineKeyboardButton("Статистика", "statistics_clicked")
            )
        )
        val sendMenuRequest = SendMenuRequest(chatId, "Основное меню", InlineKeyboardMarkup(inlineKeyboard))
        val bodySendMenu = Json.encodeToString(sendMenuRequest)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = bodySendMenu.toRequestBody(mediaType)
        val requestSendMenu = Request.Builder()
            .url(urlWithSendMenu)
            .post(requestBody)
            .build()

        return try {
            client.newCall(requestSendMenu).execute().use { responseSendMenu ->
                if (responseSendMenu.isSuccessful) {
                    responseSendMenu.body?.string()
                } else {
                    val errorMsg = "Failed to send menu: ${responseSendMenu.code} ${responseSendMenu.message}"
                    throw IOException(errorMsg)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun sendMenu(chatId: Int): String {
        val urlWithSendMenu = "$BASE_URL$botToken/sendMessage"
        val inlineKeyboardButton = listOf(
            listOf(
                InlineKeyboardButton("Изучить слова", "learn_words_clicked"),
                InlineKeyboardButton("Статистика", "statistics_clicked")
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
}
