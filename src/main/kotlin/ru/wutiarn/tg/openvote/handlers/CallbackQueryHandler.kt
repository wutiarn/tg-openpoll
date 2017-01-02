package ru.wutiarn.tg.openvote.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.CallbackQuery
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.pengrad.telegrambot.request.EditMessageReplyMarkup
import com.vdurmont.emoji.EmojiParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import ru.wutiarn.tg.openvote.makeInlineKeyboard
import java.time.Instant
import java.util.concurrent.TimeUnit

@Component
open class CallbackQueryHandler(val bot: TelegramBot,
                                val redis: RedisTemplate<String, String>) {

    val logger: Logger = LoggerFactory.getLogger(InlineQueryHandler::class.java)

    fun handleInlineCallback(callback: CallbackQuery) {
        val userId = callback.from().id()
        logger.info("Received callback: ${callback.data()} from $userId")
        var answerCallbackQuery = AnswerCallbackQuery(callback.id())

        val dataParts = callback.data().split(":")


        val method = dataParts[0]
        if (method != "v") {
            bot.execute(answerCallbackQuery.text("Unsupported data type: $method"))
            return
        }
        val id = dataParts[1]
        val voteResult = dataParts[2]

        answerCallbackQuery = when (voteResult) {
            "u" -> answerCallbackQuery.text(EmojiParser.parseToUnicode("You :thumbsup: this."))
            "n" -> answerCallbackQuery.text(EmojiParser.parseToUnicode("You :neutral_face: this."))
            "d" -> answerCallbackQuery.text(EmojiParser.parseToUnicode("You :thumbsdown: this."))
            "r" -> {
                sendResults()
                bot.execute(answerCallbackQuery.text(EmojiParser.parseToUnicode("Results were sent to you in PM")))
                return
            }
            else -> {
                bot.execute(answerCallbackQuery.text("Unsupported vote result"))
                return
            }
        }

        val votes = redis.boundHashOps<String, String>(id)
        votes.put(userId.toString(), voteResult)
        votes.expire(10, TimeUnit.DAYS)

        val voteValues = votes.values()
        val uCount = voteValues.count { it == "u" }
        val nCount = voteValues.count { it == "n" }
        val dCount = voteValues.count { it == "d" }

        bot.execute(answerCallbackQuery)

        bot.execute(EditMessageReplyMarkup(callback.inlineMessageId(), null)
                .replyMarkup(makeInlineKeyboard(id, mapOf(
                        "u" to ":thumbsup: ($uCount)",
                        "n" to ":neutral_face: ($nCount)",
                        "d" to ":thumbsdown: ($dCount)"
                ))))
    }

    private fun sendResults() {

    }

}