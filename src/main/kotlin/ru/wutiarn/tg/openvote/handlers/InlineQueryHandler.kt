package ru.wutiarn.tg.openvote.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle
import com.pengrad.telegrambot.request.AnswerInlineQuery
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.wutiarn.tg.openvote.makeInlineKeyboard

@Component
open class InlineQueryHandler(val bot: TelegramBot,
                              val redisson: RedissonClient) {

    val logger: Logger = LoggerFactory.getLogger(InlineQueryHandler::class.java)


    fun handleQuery(query: InlineQuery) {
        val queryText = query.query()
        val pollId = query.id()
        val result = InlineQueryResultArticle("id", "Start new vote", queryText)
                .description(queryText)
                .replyMarkup(makeInlineKeyboard(pollId, mapOf(
                        "u" to ":thumbsup:",
                        "n" to ":neutral_face:",
                        "d" to ":thumbsdown:"
                )))
        val resp = AnswerInlineQuery(query.id(), result)
                .cacheTime(0)
                .isPersonal(true)
        logger.info("Inline query from ${query.from().id()}: $queryText")
        bot.execute(resp)
    }
}