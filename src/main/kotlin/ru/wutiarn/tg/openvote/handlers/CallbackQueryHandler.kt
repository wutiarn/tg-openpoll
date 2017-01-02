package ru.wutiarn.tg.openvote.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.CallbackQuery
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.vdurmont.emoji.EmojiParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
open class CallbackQueryHandler(val bot: TelegramBot) {

    val logger: Logger = LoggerFactory.getLogger(InlineQueryHandler::class.java)

    fun handleInlineCallback(callback: CallbackQuery) {
        val userId = callback.from().id()
        logger.info("Received callback: ${callback.data()} from $userId")

        val dataParts = callback.data().split(":")

        var callbackDataValid = true
        var answerCallbackQuery = AnswerCallbackQuery(callback.id())

        val method = dataParts[0]
        if (method != "v") {
            answerCallbackQuery = answerCallbackQuery.text("Unsupported data type: $method")
            bot.execute(answerCallbackQuery)
            return
        }
        val id = dataParts[1]
        val action = dataParts[2]

        answerCallbackQuery = when (action) {
            "u" -> answerCallbackQuery.text(EmojiParser.parseToUnicode("You :thumbsup: this."))
            "n" -> answerCallbackQuery.text(EmojiParser.parseToUnicode("You :neutral_face: this."))
            "d" -> answerCallbackQuery.text(EmojiParser.parseToUnicode("You :thumbsdown: this."))
            "r" -> answerCallbackQuery.text(EmojiParser.parseToUnicode("Results is sent to you in PM"))
            else -> answerCallbackQuery.text("Unsupported action")
        }
        val response = bot.execute(answerCallbackQuery)
    }

}