package ru.wutiarn.tg.openvote

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL
import com.pengrad.telegrambot.model.Chat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import ru.wutiarn.tg.openvote.handlers.CallbackQueryHandler
import ru.wutiarn.tg.openvote.handlers.InlineQueryHandler
import ru.wutiarn.tg.openvote.handlers.PrivateMessageHandler

@Service
open class OpenVoteMessageRouter(
        val bot: TelegramBot,
        val inlineQueryHandler: InlineQueryHandler,
        val privateMessageHandler: PrivateMessageHandler,
        val callbackQueryHandler: CallbackQueryHandler
) {
    val logger: Logger = LoggerFactory.getLogger(OpenVoteMessageRouter::class.java)

    @Async
    open fun run() {
        logger.info("Starting telegram message router")
        bot.setUpdatesListener { updates ->
            updates.forEach { upd ->
                if (upd.message() != null) {
                    val msg = upd.message()
//                    TODO: Investigate why telegram sends private chat id when bot is mentioned in public chat
                    if (msg.chat().type() != Chat.Type.Private) {
                        logger.info("Received message from non-private chat, ignoring: $msg")
                    }
                     privateMessageHandler.handleMessage(msg)
                } else if (upd.inlineQuery() != null) {
                    inlineQueryHandler.handleQuery(upd.inlineQuery())
                } else if (upd.callbackQuery() != null) {
                    callbackQueryHandler.handleInlineCallback(upd.callbackQuery())
                } else {
                    logger.warn("Received unsupported update: $upd")
                }
            }
            return@setUpdatesListener CONFIRMED_UPDATES_ALL
        }
    }
}