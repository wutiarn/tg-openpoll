package ru.wutiarn.tg.openpoll.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.AnswerInlineQuery
import com.pengrad.telegrambot.request.SendMessage
import com.vdurmont.emoji.EmojiParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
open class PrivateMessageHandler(val bot: TelegramBot) {

    val logger: Logger = LoggerFactory.getLogger(InlineQueryHandler::class.java)

    fun handleMessage(msg: Message) {
        logger.info("Received message from ${msg.chat().id()}: ${msg.text()}")

        val resp = when(msg.text()) {
            "/help" -> "This bot can create simple open polls. Just @mention me in any chat and start typing your question"
            "/about" -> "This bot was developed by Dmitry Romanov (@wutiarn) at the beginning of 2017. Source code is " +
                    "available on [GitHub](https://github.com/wutiarn/tg-openpoll), if you'd like to see some feature - contributions are welcome"
            else -> null
        }

        resp?.let {
            bot.execute(SendMessage(msg.chat().id(), resp).parseMode(ParseMode.Markdown))
        }

    }
}