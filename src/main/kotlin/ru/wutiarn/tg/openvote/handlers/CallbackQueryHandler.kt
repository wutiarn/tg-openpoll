package ru.wutiarn.tg.openvote.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.CallbackQuery
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.vdurmont.emoji.EmojiParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
open class CallbackQueryHandler(val bot: TelegramBot,
                                val redis: RedisTemplate<String, String>) {

    val logger: Logger = LoggerFactory.getLogger(InlineQueryHandler::class.java)

    fun handleInlineCallback(callback: CallbackQuery) {
        val userId = callback.from().id()
        logger.info("Received callback: ${callback.data()} from $userId")


        val dataParts = callback.data().split(":")

        var answerCallbackQuery = AnswerCallbackQuery(callback.id())

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
        votes.expire(10, TimeUnit.SECONDS)
        votes.put(userId.toString(), voteResult)

        bot.execute(answerCallbackQuery)
    }

    private fun sendResults() {

    }

}