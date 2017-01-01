package ru.wutiarn.tg.openvote.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.CallbackQuery
import com.pengrad.telegrambot.request.SendMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
open class CallbackQueryHandler(val bot: TelegramBot) {

    val logger: Logger = LoggerFactory.getLogger(InlineQueryHandler::class.java)

    fun handleInlineCallback(callback: CallbackQuery) {
        val userId = callback.from().id()
        logger.info("Received callback: ${callback.data()} from $userId")

        val sendResponse = bot.execute(SendMessage(userId, "Vote confirmed: ${callback.data()}"))
        logger.info("Confirmation result: $sendResponse")
    }

}