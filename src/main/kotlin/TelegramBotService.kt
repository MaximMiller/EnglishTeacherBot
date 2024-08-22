package org.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import kotlinx.serialization.encodeToString

@Serializable
data class SendMessageRequest(
    val chat_id: Int,
    val text: String,
)

@Serializable
data class SendMenuRequest(
    val chat_id: Int,
    val text: String,
    val reply_markup: InlineKeyboardMarkup,
)

@Serializable
data class InlineKeyboardMarkup(
    val inline_keyboard: List<List<InlineKeyboardButton>>,
)

@Serializable
data class InlineKeyboardButton(
    val text: String,
    val callback_data: String,
)

class TelegramBotService(private val botToken: String) {
    private val client = OkHttpClient()
    fun getUpdates(updateId: Int): String? {
        val urlWithOffset = "$BASE_URL$botToken/getUpdates?offset=$updateId"
        val requestGetUpdate = Request.Builder()
            .url(urlWithOffset)
            .build()

        return try {
            client.newCall(requestGetUpdate).execute().use { responseGetUpdate ->
                if (responseGetUpdate.isSuccessful) {
                    responseGetUpdate.body?.string()
                } else {
                    val errorMsg = "Failed to get updates: ${responseGetUpdate.code} ${responseGetUpdate.message}"
                    throw IOException(errorMsg)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun sendMessage(chatId: Int, message: String): String? {
        val urlWithSendMessage = "$BASE_URL$botToken/sendMessage"
        val sendMessageRequest = SendMessageRequest(chatId, message)
        val bodySendMessage = Json.encodeToString(sendMessageRequest)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = bodySendMessage.toRequestBody(mediaType)

        val requestSendMessage = Request.Builder()
            .url(urlWithSendMessage)
            .post(requestBody)
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
}
