package ru.wutiarn.tg.openpoll.handlers

import com.pengrad.telegrambot.TelegramBot
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
open class PrivateMessageHandler(val bot: TelegramBot) {

    val logger: Logger = LoggerFactory.getLogger(InlineQueryHandler::class.java)

    fun handleMessage(msg: Message) {
        logger.info("Received message from ${msg.chat().id()}: ${msg.text()}")
    }
}