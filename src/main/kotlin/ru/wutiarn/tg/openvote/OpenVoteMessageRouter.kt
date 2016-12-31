package ru.wutiarn.tg.openvote

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL
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
                val msg = upd.message()
                logger.info("Received message from ${msg.chat().id()}: ${msg.text()}")
            }
            return@setUpdatesListener CONFIRMED_UPDATES_ALL
        }
    }
}