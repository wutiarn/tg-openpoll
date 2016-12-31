package ru.wutiarn.tg.openvote.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle
import com.pengrad.telegrambot.request.AnswerInlineQuery
import com.vdurmont.emoji.EmojiParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
open class InlineQueryHandler(val bot: TelegramBot) {

    val logger: Logger = LoggerFactory.getLogger(InlineQueryHandler::class.java)


    fun handleQuery(query: InlineQuery) {
        val qtext = query.query()
        val result = InlineQueryResultArticle("id", "Start new vote", qtext)
                .description(qtext)
                .replyMarkup(InlineKeyboardMarkup(arrayOf(
                        InlineKeyboardButton(EmojiParser.parseToUnicode(":thumbsup:"))
                                .callbackData("vote1234:up"),
                        InlineKeyboardButton(EmojiParser.parseToUnicode(":neutral_face:"))
                                .callbackData("vote1234:neutral"),
                        InlineKeyboardButton(EmojiParser.parseToUnicode(":thumbsdown:"))
                                .callbackData("vote1234:down")
                ), arrayOf(
                        InlineKeyboardButton(EmojiParser.parseToUnicode(":clipboard:"))
                                .callbackData("vote1234:results"))
                ))
        val resp = AnswerInlineQuery(query.id(), result)
                .cacheTime(0)
                .isPersonal(true)
        logger.info("Inline query from ${query.from().id()}: $qtext")
        bot.execute(resp)
    }
}