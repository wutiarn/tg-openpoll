package ru.wutiarn.tg.openvote

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle
import com.pengrad.telegrambot.request.AnswerInlineQuery
import com.vdurmont.emoji.EmojiParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
open class OpenVoteMessageRouter(val bot: TelegramBot) {

    val logger: Logger = LoggerFactory.getLogger(OpenVoteMessageRouter::class.java)

    @Async
    open fun run() {
        logger.info("Starting telegram message router")
        bot.setUpdatesListener { updates ->
            updates.forEach { upd ->
                if (upd.message() != null) {
                    val msg = upd.message()
                    logger.info("Received message from ${msg.chat().id()}: ${msg.text()}")
                } else if (upd.inlineQuery() != null) {
                    val query = upd.inlineQuery()
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
            return@setUpdatesListener CONFIRMED_UPDATES_ALL
        }
    }
}