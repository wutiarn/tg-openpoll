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
        val queryText = query.query()
        val pollId = query.id()
        val result = InlineQueryResultArticle("id", "Start new vote", queryText)
                .description(queryText)
                .replyMarkup(InlineKeyboardMarkup(arrayOf(
                        InlineKeyboardButton(EmojiParser.parseToUnicode(":thumbsup:"))
                                .callbackData("v:$pollId:u"),
                        InlineKeyboardButton(EmojiParser.parseToUnicode(":neutral_face:"))
                                .callbackData("v:$pollId:n"),
                        InlineKeyboardButton(EmojiParser.parseToUnicode(":thumbsdown:"))
                                .callbackData("v:$pollId:d")
                ), arrayOf(
                        InlineKeyboardButton("Results")
                                .callbackData("v:$pollId:r"))
                ))
        val resp = AnswerInlineQuery(query.id(), result)
                .cacheTime(0)
                .isPersonal(true)
        logger.info("Inline query from ${query.from().id()}: $queryText")
        bot.execute(resp)
    }
}