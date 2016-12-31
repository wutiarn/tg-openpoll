package ru.wutiarn.tg.openvote.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.CallbackQuery
import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle
import com.pengrad.telegrambot.request.AnswerInlineQuery
import com.vdurmont.emoji.EmojiParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
open class CallbackQueryHandler(val bot: TelegramBot) {

    val logger: Logger = LoggerFactory.getLogger(InlineQueryHandler::class.java)

    fun handleInlineCallback(callback: CallbackQuery) {
        logger.info("Received callback: ${callback.data()}")
    }

}