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
        val answerCallbackQuery = AnswerCallbackQuery(callback.id())

        val dataParts = callback.data().split(":")

        val method = dataParts[0]
        if (method != "v") {
            bot.execute(answerCallbackQuery.text("Unsupported data type: $method"))
            return
        }
        val id = dataParts[1]

        if (!redis.hasKey("openvote_$id")) {
            bot.execute(answerCallbackQuery.text("This vote is finished"))
            return
        }

        val selectedVariantIndex = try {
            dataParts[2].toInt()
        } catch (e: Exception) {
            bot.execute(answerCallbackQuery.text("Failed to parse selected option"))
            return
        }

        val poll = redis.boundHashOps<String, String>("openvote_${id}")
        val votes = redis.boundHashOps<String, String>("openvote_${id}_votes")
        val variants = redis.boundListOps("openvote_${id}_variants").let { it.range(0, it.size() - 1) }

        votes.expire(10, TimeUnit.DAYS)

        if (selectedVariantIndex !in 0..variants.lastIndex) {
            bot.execute(answerCallbackQuery.text("Unsupported vote result"))
            return
        }
        val selectedVariant = variants[selectedVariantIndex]

        bot.execute(answerCallbackQuery.text(EmojiParser.parseToUnicode("You $selectedVariant this.")))

        votes.put(userId.toString(), selectedVariantIndex.toString())

        val voteValues = votes.values()


        val variantButtons = variants
                .mapIndexed { i, s -> "$s (${voteValues.count {it == i.toString()}})" }
                .toTypedArray()

        bot.execute(EditMessageText(callback.inlineMessageId(),
                "*${poll["topic"]}*\n\n${getResults(votes.entries(), variants)}")
                .replyMarkup(makeInlineKeyboard(id, variantButtons))
                .parseMode(ParseMode.Markdown)
                .disableWebPagePreview(true)
        )
    }

    private fun getResults(votes: Map<String, String>, variants: List<String>): String {
        return votes.entries.sortedBy { it.value }
                .map { vote ->
                    val chatResponse = bot.execute(GetChat(vote.key))
                    if (!chatResponse.isOk) {
                        return@map "<unknown> (${vote.key})"
                    }
                    val chat = chatResponse.chat()
                    val username = chat.username()
                    val selectedVariant = EmojiParser.parseToUnicode(variants[vote.value.toInt()])
                    val userDisplayName = "${chat.firstName()} ${chat.lastName()}"

                    return@map when (username != null) {
                        true -> "$selectedVariant [$userDisplayName](https://telegram.me/$username)"
                        false -> "$selectedVariant $userDisplayName"
                    }
                }
                .joinToString("\n")
    }
}