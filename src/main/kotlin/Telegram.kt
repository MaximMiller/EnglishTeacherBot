package org.example

const val BASE_URL = "https://api.telegram.org/bot"

fun main(argument: Array<String>) {
    val botToken = argument[0]
    var nextUpdateId = 0
    var chatId = 0
    val telegramBotService = TelegramBotService()

    while (true) {
        Thread.sleep(1000)
        val updates = telegramBotService.getUpdates(botToken, nextUpdateId)
        val updateIdRegex = Regex(pattern = "\"update_id\":\\s*(\\d+)")
        val chatIdRegex = Regex(pattern = "\"chat\":\\{\"id\":\\s*(\\d+)")
        val matchesUpdateId = updateIdRegex.find(updates)
        val matchesChatId = chatIdRegex.find(updates)
        println(updates)
        if (matchesUpdateId != null) {
            val updateId = matchesUpdateId.groupValues[1].toInt()
            nextUpdateId = updateId + 1
            if (matchesChatId != null) {
                chatId = matchesChatId.groupValues[1].toInt()
            }
            telegramBotService.sendMessage(botToken,chatId,"Hello")
        }
    }
}
