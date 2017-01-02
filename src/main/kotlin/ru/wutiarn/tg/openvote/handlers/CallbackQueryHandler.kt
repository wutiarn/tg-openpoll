package ru.wutiarn.tg.openvote.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.CallbackQuery
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.AnswerCallbackQuery
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.GetChat
import com.vdurmont.emoji.EmojiParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import ru.wutiarn.tg.openvote.makeInlineKeyboard
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

        if (!redis.hasKey("openvote_$id")) {
            bot.execute(answerCallbackQuery.text("This vote is finished"))
            return
        }

        val poll = redis.boundHashOps<String, String>("openvote_${id}")
        val votes = redis.boundHashOps<String, String>("openvote_${id}_votes")

        answerCallbackQuery = when (voteResult) {
            "u" -> answerCallbackQuery.text(EmojiParser.parseToUnicode("You :thumbsup: this."))
            "n" -> answerCallbackQuery.text(EmojiParser.parseToUnicode("You :neutral_face: this."))
            "d" -> answerCallbackQuery.text(EmojiParser.parseToUnicode("You :thumbsdown: this."))
            else -> {
                bot.execute(answerCallbackQuery.text("Unsupported vote result"))
                return
            }
        }

        votes.put(userId.toString(), voteResult)
        votes.expire(10, TimeUnit.DAYS)

        val voteValues = votes.values()
        val uCount = voteValues.count { it == "u" }
        val nCount = voteValues.count { it == "n" }
        val dCount = voteValues.count { it == "d" }

        bot.execute(answerCallbackQuery)

        bot.execute(EditMessageText(callback.inlineMessageId(),
                "*${poll["topic"]}*\n\nCurrent results:\n${getResults(votes.entries())}")
                .replyMarkup(makeInlineKeyboard(id, mapOf(
                        "u" to ":thumbsup: ($uCount)",
                        "n" to ":neutral_face: ($nCount)",
                        "d" to ":thumbsdown: ($dCount)"
                )))
                .parseMode(ParseMode.Markdown)
        )
    }

    private fun getResults(votes: Map<String, String>): String {
        return votes.entries.sortedBy { it.value }
                .map { vote ->
                    val chatResponse = bot.execute(GetChat(vote.key))
                    if (!chatResponse.isOk) {
                        return@map "<unknown> (${vote.key})"
                    }
                    val chat = chatResponse.chat()
                    val username = chat.username()
                    return@map "[[${vote.value}]] ${chat.firstName()} ${chat.lastName()} ${if (username != null) "@" + username else ""}"
                }
                .joinToString("\n")
    }
}