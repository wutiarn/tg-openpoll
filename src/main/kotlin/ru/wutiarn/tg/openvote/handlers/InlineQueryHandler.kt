package ru.wutiarn.tg.openvote.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle
import com.pengrad.telegrambot.model.request.InputTextMessageContent
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.AnswerInlineQuery
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import ru.wutiarn.tg.openvote.makeInlineKeyboard
import java.util.*
import java.util.concurrent.TimeUnit

@Component
open class InlineQueryHandler(val bot: TelegramBot,
                              val redisTemplate: StringRedisTemplate) {

    val logger: Logger = LoggerFactory.getLogger(InlineQueryHandler::class.java)

    fun handleQuery(query: InlineQuery) {
        val queryText = query.query()
        val pollId = UUID.randomUUID().toString()
        val variants = arrayOf(
                ":thumbsup:",
                ":neutral_face:",
                ":thumbsdown:"
        )

        val msg = InputTextMessageContent("*$queryText*")
                .parseMode(ParseMode.Markdown)
                .disableWebPagePreview(true)

        val result = InlineQueryResultArticle(pollId, "Start new vote", msg)
                .description(queryText)
                .replyMarkup(makeInlineKeyboard(pollId, variants))
        val resp = AnswerInlineQuery(query.id(), result)
                .cacheTime(0)
                .isPersonal(true)

        val pollRedis = redisTemplate.boundHashOps<String, String>("openvote_$pollId")
        pollRedis.put("topic", queryText)
        pollRedis.expire(10, TimeUnit.DAYS)

        val pollVariants = redisTemplate.boundListOps("openvote_${pollId}_variants")
        pollVariants.rightPushAll(*variants)
        pollVariants.expire(10, TimeUnit.DAYS)

        logger.info("Inline query from ${query.from().id()}: $queryText")
        bot.execute(resp)
    }
}