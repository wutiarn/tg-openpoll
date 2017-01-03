package ru.wutiarn.tg.openpoll.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle
import com.pengrad.telegrambot.model.request.InputTextMessageContent
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.AnswerInlineQuery
import com.vdurmont.emoji.EmojiParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import ru.wutiarn.tg.openpoll.makeInlineKeyboard
import java.util.*
import java.util.concurrent.TimeUnit

@Component
open class InlineQueryHandler(val bot: TelegramBot,
                              val redisTemplate: StringRedisTemplate) {

    val logger: Logger = LoggerFactory.getLogger(InlineQueryHandler::class.java)

    fun handleQuery(query: InlineQuery) {
        val queryText = query.query()

        val resultArticlesContent = listOf(
                ResultArticleContent("Start :thumbsup: / :thumbsdown: poll",
                        arrayOf(":thumbsup:", ":thumbsdown:")),
                ResultArticleContent("Start :thumbsup: / :neutral_face: / :thumbsdown: poll",
                        arrayOf(":thumbsup:", ":neutral_face:", ":thumbsdown:")),
                ResultArticleContent("Start :ok_hand: poll",
                        arrayOf(":ok_hand:"))
        )

        logger.info("Inline query ${query.id()} from ${query.from().id()}: $queryText")

        val resultArticles = resultArticlesContent.map { articleContent ->
            val pollId = UUID.randomUUID().toString()

            val msg = InputTextMessageContent("*$queryText*")
                    .parseMode(ParseMode.Markdown)
                    .disableWebPagePreview(true)

            val resultArticle = InlineQueryResultArticle(pollId, EmojiParser.parseToUnicode(articleContent.title), msg)
                    .description(queryText)
                    .replyMarkup(makeInlineKeyboard(pollId, articleContent.variants))

            val pollRedis = redisTemplate.boundHashOps<String, String>("openpoll_$pollId")
            pollRedis.put("topic", queryText)
            pollRedis.expire(10, TimeUnit.DAYS)

            val pollVariants = redisTemplate.boundListOps("openpoll_${pollId}_variants")
            pollVariants.rightPushAll(*articleContent.variants)
            pollVariants.expire(10, TimeUnit.DAYS)

            logger.info("Created poll $pollId for query ${query.id()}")

            return@map resultArticle
        }.toTypedArray()

        val resp = AnswerInlineQuery(query.id(), *resultArticles)
                .cacheTime(0)
                .isPersonal(true)

        bot.execute(resp)
    }

    @Suppress("ArrayInDataClass")
    data class ResultArticleContent(
            val title: String,
            val variants: Array<String>
    )
}